/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.actionserrorssetter.ActionsErrorsSetter
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.*
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.BestActionSelector
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.FeaturesErrors
import com.kotlinnlp.syntaxdecoder.utils.scheduling.BatchScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.EpochScheduling
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractorTrainable
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.utils.DecodingContext
import com.kotlinnlp.syntaxdecoder.modules.supportstructure.DecodingSupportStructure
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.utils.Updatable

/**
 * The helper to train the trainable components of a SyntaxDecoder (the actions scorer and the features extractor).
 *
 */
class SyntaxDecoderTrainer<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesErrorsType: FeaturesErrors,
  FeaturesType : Features<FeaturesErrorsType, *>,
  out SupportStructureType : DecodingSupportStructure>
(
  private val syntaxDecoder: SyntaxDecoder<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    SupportStructureType>,
  private val actionsErrorsSetter: ActionsErrorsSetter<StateType, TransitionType, ItemType, InputContextType>,
  private val bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, InputContextType>,
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
   *
   */
  private val transitionSystem = this.syntaxDecoder.transitionSystem

  /**
   *
   */
  private val actionsGenerator = this.syntaxDecoder.actionsGenerator

  /**
   *
   */
  private val featuresExtractor = this.syntaxDecoder.featuresExtractor

  /**
   *
   */
  @Suppress("UNCHECKED_CAST")
  private val actionsScorer = this.syntaxDecoder.actionsScorer as ActionsScorerTrainable<StateType, TransitionType,
    InputContextType, ItemType, FeaturesErrorsType, FeaturesType, SupportStructureType>

  /**
   *
   */
  private val supportStructureFactory = this.syntaxDecoder.supportStructureFactory


  /**
   * The support structure of the [actionsScorer].
   */
  private val supportStructure: SupportStructureType
    = this.supportStructureFactory.globalStructure()

  /**
   * Learn from a single example composed by a list of items and the expected gold [DependencyTree].
   * The best local action is applied with a greedy approach until the final state is reached.
   * Before applying it, the temporary result is compared to the gold [DependencyTree] to
   *
   * @param context a generic [InputContext] used to decode
   * @param goldDependencyTree the gold [DependencyTree]
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   * @param beforeApplyAction callback called before applying the best action (default = null)
   *
   * @return the [DependencyTree] built following a greedy approach
   */
  fun learn(context: InputContextType,
            goldDependencyTree: DependencyTree,
            propagateToInput: Boolean,
            beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                 context: InputContextType) -> Unit)? = null): DependencyTree {

    val state: StateType = this.transitionSystem.getInitialState(
      itemIds = context.getInitialStateItemIds(),
      size = context.items.size)

    val extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType> = ExtendedState(
      state = state,
      context = context,
      oracle = this.oracleFactory(goldDependencyTree),
      scoreAccumulator = this.syntaxDecoder.scoreAccumulatorFactory())

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
  private fun processState(extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
                           propagateToInput: Boolean,
                           beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                                context: InputContextType) -> Unit)?) {

    val decodingContext = DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>(
      actions = this.actionsGenerator.generateFrom(this.transitionSystem.generateTransitions(extendedState.state)),
      extendedState = extendedState)

    this.scoreActions(decodingContext = decodingContext)

    this.calculateAndPropagateErrors(decodingContext = decodingContext, propagateToInput = propagateToInput)

    this.applyAction(
      action = this.bestActionSelector.select(
        sortedActions = decodingContext.sortedActions,
        extendedState = extendedState),
      extendedState = extendedState,
      beforeApplyAction = beforeApplyAction)
  }

  /**
   * Score the actions allowed in a given state.
   *
   * @param decodingContext the decoding context in which to set the extracted features and scored actions
   */
  private fun scoreActions(
    decodingContext: DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>
  ) {

    this.featuresExtractor.setFeatures(
      decodingContext = decodingContext,
      supportStructure = this.supportStructure)

    this.actionsScorer.score(
      decodingContext = decodingContext,
      supportStructure = this.supportStructure)
  }

  /**
   * Calculate and propagate the errors of the actions in the given support structure respect to a current state.
   *
   * @param decodingContext the decoding context that contains the extracted features and the scored actions
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   */
  private fun calculateAndPropagateErrors(
    decodingContext: DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>,
    propagateToInput: Boolean){

    this.actionsErrorsSetter.setErrors(
      sortedActions = decodingContext.sortedActions,
      extendedState = decodingContext.extendedState)

    if (this.actionsErrorsSetter.areErrorsRelevant) {

      this.relevantErrorsCount++

      this.actionsScorer.backward(
        decodingContext = decodingContext,
        supportStructure = this.supportStructure)

      if (propagateToInput && this.featuresExtractor is FeaturesExtractorTrainable) {

        decodingContext.features.errors = this.actionsScorer.getFeaturesErrors(
          decodingContext = decodingContext,
          supportStructure = this.supportStructure)

        this.featuresExtractor.backward(
          decodingContext = decodingContext,
          supportStructure = this.supportStructure)
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
                          extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
                          beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                               context: InputContextType) -> Unit)?) {

    beforeApplyAction?.invoke(action, extendedState.context)  // external callback

    extendedState.oracle!!.apply(action.transition)
    extendedState.addAction(action)

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
