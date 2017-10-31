/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.modules.actionserrorssetter.ActionsErrorsSetter
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.*
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.BestActionSelector
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.FeaturesErrors
import com.kotlinnlp.syntaxdecoder.utils.scheduling.BatchScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.EpochScheduling
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractorMemory
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractorStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractorTrainable
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.syntax.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.utils.Updatable

/**
 * The helper to train the trainable components of a SyntaxDecoder (the actions scorer and the features extractor).
 *
 */
class SyntaxDecoderTrainer<
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
            beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                 context: ContextType) -> Unit)? = null): DependencyTree {

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
                           beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                                context: ContextType) -> Unit)?) {

    val actionsScorerMemory = this.actionsScorerStructure.buildMemoryOf(
      actions = this.actionsGenerator.generateFrom(
        transitions = this.transitionSystem.generateTransitions(extendedState.state)),
      extendedState = extendedState)

    val featuresExtractorMemory = this.featuresExtractorStructure.buildMemoryOf(
      extendedState = extendedState,
      stateView = this.actionsScorer.buildStateView(actionsScorerMemory))

    this.scoreActions(
      actionsMemory = actionsScorerMemory,
      featuresMemory = featuresExtractorMemory)

    this.calculateAndPropagateErrors(
      featuresMemory = featuresExtractorMemory,
      actionsMemory = actionsScorerMemory,
      propagateToInput = propagateToInput)

    this.applyAction(
      action = this.bestActionSelector.select(
        actions = actionsScorerMemory.sortedActions,
        extendedState = extendedState),
      extendedState = extendedState,
      beforeApplyAction = beforeApplyAction)
  }

  /**
   * Score the actions allowed in a given state, assigns them a score.
   *
   * @param featuresMemory the dynamic support structure of the [featuresExtractor]
   * @param actionsMemory the dynamic support structure of the [actionsScorer]
   */
  private fun scoreActions(
    featuresMemory: FeaturesExtractorMemory<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
    actionsMemory: ActionsScorerMemory<
      StateType, TransitionType, ContextType, ItemType, ActionsScorerStructureType>) {

    this.featuresExtractor.setFeatures(featuresMemory)

    this.actionsScorer.score(features = featuresMemory.features, structure = actionsMemory)
  }

  /**
   * Calculate and propagate the errors of the actions in the given support structure respect to a current state.
   *
   * @param featuresMemory the dynamic support structure of the [featuresExtractor]
   * @param actionsMemory the dynamic support structure of the [actionsScorer]
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   */
  private fun calculateAndPropagateErrors(
    featuresMemory: FeaturesExtractorMemory<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
    actionsMemory: ActionsScorerMemory<
      StateType, TransitionType, ContextType, ItemType, ActionsScorerStructureType>,
    propagateToInput: Boolean){

    this.actionsErrorsSetter.setErrors(
      actions = actionsMemory.sortedActions,
      extendedState = actionsMemory.extendedState)

    if (this.actionsErrorsSetter.areErrorsRelevant) {

      this.relevantErrorsCount++

      this.actionsScorer.backward(structure = actionsMemory, propagateToInput = propagateToInput)

      if (propagateToInput && this.featuresExtractor is FeaturesExtractorTrainable) {
        featuresMemory.features.errors = this.actionsScorer.getFeaturesErrors(actionsMemory)
        this.featuresExtractor.backward(featuresMemory = featuresMemory, propagateToInput = propagateToInput)
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
                          beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                               context: ContextType) -> Unit)?) {

    beforeApplyAction?.invoke(action, extendedState.context)  // external callback

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
