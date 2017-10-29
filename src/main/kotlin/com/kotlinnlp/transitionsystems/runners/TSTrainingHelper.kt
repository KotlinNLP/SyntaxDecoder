/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.runners

import com.kotlinnlp.transitionsystems.OracleFactory
import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.TransitionSystem
import com.kotlinnlp.transitionsystems.helpers.ActionsGenerator
import com.kotlinnlp.transitionsystems.helpers.BestActionSelector
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.*
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.FeaturesErrors
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.BatchScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.EpochScheduling
import com.kotlinnlp.transitionsystems.helpers.sortByScoreAndPriority
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.syntax.DependencyTree

/**
 * The helper to train the trainable components of a Transition System (the actions scorer and the features extractor).
 *
 */
class TSTrainingHelper<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesErrorsType: FeaturesErrors,
  FeaturesType : Features<FeaturesErrorsType, *>,
  FeaturesExtractorStructureType: FeaturesExtractorStructure<
    FeaturesExtractorStructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>,
  ActionsScorerStructureType: ActionsScorerStructure<
    ActionsScorerStructureType, StateType, TransitionType, ContextType, ItemType>>
(
  private val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val featuresExtractor: FeaturesExtractor<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
  private val actionsScorer: ActionsScorerTrainable<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesErrorsType, FeaturesType,
    ActionsScorerStructureType>,
  private val actionsErrorsSetter: ActionsErrorsSetter<StateType, TransitionType, ItemType, ContextType>,
  private val bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>,
  private val oracleFactory: OracleFactory<StateType, TransitionType>
) :
  BatchScheduling,
  EpochScheduling,
  Updatable {

  /**
   * Count the number of relevant errors.
   */
  var relevantErrorsCount: Int = 0
    private set

  /**
   * The support structure of the [featuresExtractor].
   */
  private val featuresExtractorStructure = this.featuresExtractor.supportStructureFactory()

  /**
   * The support structure of the [actionsScorer].
   */
  private val actionsScorerStructure = this.actionsScorer.supportStructureFactory()

  /**
   * Learn from a single example composed by a list of items and the expected gold [DependencyTree].
   * The best local action is applied with a greedy approach until the final state is reached.
   * Before applying it, the temporary result is compared to the gold [DependencyTree] to
   *
   * @param context a generic [DecodingContext] used to decode
   * @param goldDependencyTree the gold [DependencyTree]
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   * @param beforeApplyAction callback called before applying the best action (default = null)
   *
   * @return the [DependencyTree] built following a greedy approach
   */
  fun learn(context: ContextType,
            goldDependencyTree: DependencyTree,
            propagateToInput: Boolean,
            beforeApplyAction: ((
              action: Transition<TransitionType, StateType>.Action,
              extendedState: ExtendedState<
                StateType, TransitionType, ItemType, ContextType>) -> Unit)? = null): DependencyTree {

    val state: StateType = this.transitionSystem.getInitialState(context.items.map { it.id } )

    val extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType> = ExtendedState(
      state = state,
      context = context,
      oracle = this.oracleFactory(goldDependencyTree))

    while (!state.isTerminal) {

      this.actionsScorer.newExample()

      if (this.featuresExtractor is FeaturesExtractorTrainable) {
        this.featuresExtractor.newExample()
      }

      this.processState(
        extendedState = extendedState,
        propagateToInput = propagateToInput,
        beforeApplyAction = beforeApplyAction)
    }

    return state.dependencyTree
  }

  /**
   * Forward the system processing a state, calculate and propagate errors, apply the best action.
   *
   * @param extendedState the [ExtendedState] context of the state
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   * @param beforeApplyAction callback called before applying the best action (default = null)
   */
  private fun processState(extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
                           propagateToInput: Boolean,
                           beforeApplyAction: ((
                             action: Transition<TransitionType, StateType>.Action,
                             extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)?
  ) {


    val actions: List<Transition<TransitionType, StateType>.Action> = this.actionsGenerator
      .generateFrom(transitions = this.transitionSystem.generateTransitions(extendedState.state))

    val actionsDynamicStructure = this.actionsScorerStructure.dynamicStructureFactory(
      actions = actions,
      extendedState = extendedState)

    val featuresDynamicStructure = this.featuresExtractorStructure.dynamicStructureFactory(
      extendedState = extendedState,
      stateView = this.actionsScorer.buildStateView(actionsDynamicStructure))

    this.scoreActions(
      actionsDynamicStructure = actionsDynamicStructure,
      featuresDynamicStructure = featuresDynamicStructure)

    val sortedActionsDynamicStructure = this.actionsScorerStructure.dynamicStructureFactory(
      actions = actionsDynamicStructure.actions.sortByScoreAndPriority(),
      extendedState = extendedState)

    this.calculateAndPropagateErrors(
      featuresDynamicStructure = featuresDynamicStructure,
      actionsDynamicStructure = sortedActionsDynamicStructure,
      propagateToInput = propagateToInput)

    this.applyAction(
      action = this.bestActionSelector.select(actions = actions, extendedState = extendedState),
      extendedState = extendedState,
      beforeApplyAction = beforeApplyAction)
  }

  /**
   * Score the actions allowed in a given state, assigns them a score.
   *
   * @param featuresDynamicStructure the dynamic support structure of the [featuresExtractor]
   * @param actionsDynamicStructure the dynamic support structure of the [actionsScorer]
   */
  private fun scoreActions(
    featuresDynamicStructure: FeaturesExtractorDynamicStructure<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
    actionsDynamicStructure: ActionsScorerDynamicStructure<
      StateType, TransitionType, ContextType, ItemType, ActionsScorerStructureType>) {

    this.featuresExtractor.setFeatures(featuresDynamicStructure)

    this.actionsScorer.score(features = featuresDynamicStructure.features, structure = actionsDynamicStructure)
  }

  /**
   * Calculate and propagate the errors of the actions in the given support structure respect to a current state.
   *
   * @param featuresDynamicStructure the dynamic support structure of the [featuresExtractor]
   * @param actionsDynamicStructure the dynamic support structure of the [actionsScorer]
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   */
  private fun calculateAndPropagateErrors(
    featuresDynamicStructure: FeaturesExtractorDynamicStructure<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
    actionsDynamicStructure: ActionsScorerDynamicStructure<
      StateType, TransitionType, ContextType, ItemType, ActionsScorerStructureType>,
    propagateToInput: Boolean){

    this.actionsErrorsSetter.setErrors(
      actions = actionsDynamicStructure.actions,
      extendedState = actionsDynamicStructure.extendedState)

    if (this.actionsErrorsSetter.areErrorsRelevant) {

      this.relevantErrorsCount++

      this.actionsScorer.backward(structure = actionsDynamicStructure, propagateToInput = propagateToInput)

      if (propagateToInput && this.featuresExtractor is FeaturesExtractorTrainable) {
        featuresDynamicStructure.features.errors = this.actionsScorer.getFeaturesErrors(actionsDynamicStructure)
        this.featuresExtractor.backward(structure = featuresDynamicStructure, propagateToInput = propagateToInput)
      }
    }
  }

  /**
   * Apply a given [action] causing an update of the state and the oracle.
   *
   * @param action the action to apply
   * @param extendedState the [ExtendedState] context of the state (including the oracle)
   * @param beforeApplyAction callback called before applying the [action] (default = null)
   */
  private fun applyAction(action: Transition<TransitionType, StateType>.Action,
                          extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
                          beforeApplyAction: ((
                            action: Transition<TransitionType, StateType>.Action,
                            extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)?) {

    beforeApplyAction?.invoke(action, extendedState)  // external callback

    extendedState.oracle!!.updateWith(action.transition)

    action.apply()
  }

  /**
   * Beat the occurrence of a new batch
   */
  override fun newBatch() {

    this.actionsScorer.newBatch()

    if (this.featuresExtractor is FeaturesExtractorTrainable) {
      this.featuresExtractor.newBatch()
    }
  }

  /**
   * Beat the occurrence of a new epoch
   */
  override fun newEpoch() {

    this.actionsScorer.newEpoch()

    if (this.featuresExtractor is FeaturesExtractorTrainable) {
      this.featuresExtractor.newEpoch()
    }
  }

  /**
   * Update the trainable components
   */
  override fun update() {

    if (this.relevantErrorsCount > 0) {

      this.actionsScorer.update()

      if (this.featuresExtractor is FeaturesExtractorTrainable) {
        this.featuresExtractor.update()
      }

      this.relevantErrorsCount = 0
    }
  }
}
