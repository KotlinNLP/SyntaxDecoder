/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.supportstructure.DecodingSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorer
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.MultiActionsSelector
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.supportstructure.SupportStructureFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.utils.DaemonThread
import com.kotlinnlp.syntaxdecoder.utils.DecodingContext

/**
 * The BeamDecoder decodes the syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * More transitions can be applied to a state, following a fixed number of ways in a beam of parallel states. A score is
 * assigned to each state and the one with higher score is chosen as final state.
 *
 * @param transitionSystem a transition system
 * @param actionsGenerator an actions generator
 * @param featuresExtractor a features extractor
 * @param actionsScorer an actions scorer
 * @param multiActionsSelector a multiple actions selector
 * @param supportStructureFactory a support structure factory
 */
class BeamDecoderThread<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  out SupportStructureType : DecodingSupportStructure>
(
  private val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val featuresExtractor: FeaturesExtractor<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    SupportStructureType>,
  private val actionsScorer: ActionsScorer<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    SupportStructureType>,
  private val multiActionsSelector: MultiActionsSelector<StateType, TransitionType, ItemType, InputContextType>,
  private val supportStructureFactory: SupportStructureFactory<SupportStructureType>
) :
  DaemonThread<
    BeamDecoderThreadInput<StateType, TransitionType, InputContextType, ItemType>,
    List<Transition<TransitionType, StateType>.Action>>() {

  /**
   * The global support structure to score actions and extract features.
   */
  private val supportStructure: SupportStructureType
    = this.supportStructureFactory.globalStructure()

  /**
   * Called every time a new input is written into this thread.
   * Process it and produce a new output.
   */
  override fun processInput() {
    this.processState(extendedState = this.inputValue.extendedState, scoreThreshold = this.inputValue.scoreThreshold)
  }

  /**
   * Get the list of best actions to apply, given the scoring support structure and an [ExtendedState].
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param scoreThreshold the minimum score threshold (must be <= 0.0 or null for -inf)
   *
   * @return the list of best actions to apply to the given state
   */
  private fun processState(extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
                           scoreThreshold: Double?) {

    val scoredActions: List<Transition<TransitionType, StateType>.Action> = this.getScoredActions(
      supportStructure = this.supportStructure,
      extendedState = extendedState)

    this.outputValue = this.multiActionsSelector.select(
      sortedActions = scoredActions,
      extendedState = extendedState,
      scoreThreshold = scoreThreshold)
  }

  /**
   * Generate the possible actions allowed in a given state, assigns them a score and returns them in descending order
   * according to the score.
   *
   * @param supportStructure the scoring support structure
   * @param extendedState the [ExtendedState] containing items, context and state
   *
   * @return a list of scored actions, sorted by descending score and then by transition priority
   */
  private fun getScoredActions(
    supportStructure: SupportStructureType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>
  ): List<Transition<TransitionType, StateType>.Action> {

    val decodingContext = DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>(
      actions = this.actionsGenerator.generateFrom(this.transitionSystem.generateTransitions(extendedState.state)),
      extendedState = extendedState)

    this.featuresExtractor.setFeatures(decodingContext = decodingContext, supportStructure = supportStructure)

    this.actionsScorer.score(decodingContext = decodingContext, supportStructure = supportStructure)

    return decodingContext.sortedActions
  }
}
