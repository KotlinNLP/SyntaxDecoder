/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.dependencytree.DependencyTree
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
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractorTrainable
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringGlobalSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.SupportStructuresFactory
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
  FeaturesErrorsType: FeaturesErrors,
  FeaturesType : Features<FeaturesErrorsType, *>,
  out ScoringGlobalStructureType: ScoringGlobalSupportStructure,
  ScoringStructureType : ScoringSupportStructure<StateType, TransitionType, ContextType, ItemType,
    FeaturesType, ScoringGlobalStructureType>>
(
  private val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val featuresExtractor: FeaturesExtractor<StateType, TransitionType, ContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType, ScoringStructureType>,
  private val actionsScorer: ActionsScorerTrainable<StateType, TransitionType, ContextType, ItemType,
    FeaturesErrorsType, FeaturesType, ScoringGlobalStructureType, ScoringStructureType>,
  private val actionsErrorsSetter: ActionsErrorsSetter<StateType, TransitionType, ItemType, ContextType>,
  private val bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>,
  private val oracleFactory: OracleFactory<StateType, TransitionType>,
  private val supportStructuresFactory: SupportStructuresFactory<StateType, TransitionType, ContextType, ItemType,
    FeaturesType, ScoringGlobalStructureType, ScoringStructureType>
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
   * The support structure of the [actionsScorer].
   */
  private val scoringGlobalSupportStructure: ScoringGlobalStructureType
    = this.supportStructuresFactory.globalStructure()

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

    val scoringSupportStructure = this.supportStructuresFactory.localStructure(
      scoringGlobalSupportStructure = this.scoringGlobalSupportStructure,
      actions = this.actionsGenerator.generateFrom(
        transitions = this.transitionSystem.generateTransitions(extendedState.state)),
      extendedState = extendedState)

    this.scoreActions(structure = scoringSupportStructure)

    this.calculateAndPropagateErrors(structure = scoringSupportStructure, propagateToInput = propagateToInput)

    this.applyAction(
      action = this.bestActionSelector.select(
        sortedActions = scoringSupportStructure.sortedActions,
        extendedState = extendedState),
      extendedState = extendedState,
      beforeApplyAction = beforeApplyAction)
  }

  /**
   * Score the actions allowed in a given state.
   *
   * @param structure the scoring support structure
   */
  private fun scoreActions(structure: ScoringStructureType) {

    this.featuresExtractor.setFeatures(structure)
    this.actionsScorer.score(structure)
  }

  /**
   * Calculate and propagate the errors of the actions in the given support structure respect to a current state.
   *
   * @param structure the scoring support structure
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   */
  private fun calculateAndPropagateErrors(structure: ScoringStructureType, propagateToInput: Boolean){

    this.actionsErrorsSetter.setErrors(
      sortedActions = structure.sortedActions,
      extendedState = structure.extendedState)

    if (this.actionsErrorsSetter.areErrorsRelevant) {

      this.relevantErrorsCount++

      this.actionsScorer.backward(structure = structure, propagateToInput = propagateToInput)

      if (propagateToInput && this.featuresExtractor is FeaturesExtractorTrainable) {
        structure.features.errors = this.actionsScorer.getFeaturesErrors(structure)
        this.featuresExtractor.backward(structure = structure, propagateToInput = propagateToInput)
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
