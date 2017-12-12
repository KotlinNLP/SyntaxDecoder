/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorer
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringGlobalSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.SupportStructuresFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.scoreaccumulator.ScoreAccumulator
import com.kotlinnlp.syntaxdecoder.utils.DecodingContext

/**
 * The [SyntaxDecoder] decodes the implicit syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * @property transitionSystem a [TransitionSystem]
 * @property actionsGenerator an actions generator
 * @property featuresExtractor a features extractor
 * @property actionsScorer an actions scorer
 * @property supportStructuresFactory a support structures factory
 * @property scoreAccumulatorFactory a factory of score accumulators
 */
abstract class SyntaxDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  ScoringGlobalStructureType : ScoringGlobalSupportStructure>
(
  val transitionSystem: TransitionSystem<StateType, TransitionType>,
  val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  val featuresExtractor: FeaturesExtractor<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType>,
  val actionsScorer: ActionsScorer<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType>,
  val supportStructuresFactory: SupportStructuresFactory<ScoringGlobalStructureType>,
  val scoreAccumulatorFactory: ScoreAccumulator.Factory
) {

  /**
   * Decode the syntax of the given items building a dependency tree.
   *
   * @param context a generic [InputContext] used to decode
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a [DependencyTree]
   */
  fun decode(
    context: InputContextType,
    beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                         context: InputContextType) -> Unit)? = null): DependencyTree {

    val extendedState = ExtendedState<StateType, TransitionType, ItemType, InputContextType>(
      state = this.transitionSystem.getInitialState(
        itemIds = context.getInitialStateItemsId(),
        size = context.length),
      context = context,
      oracle = null,
      scoreAccumulator = this.scoreAccumulatorFactory())

    return this.processState(extendedState = extendedState, beforeApplyAction = beforeApplyAction)
  }

  /**
   * Decode the syntax starting from an initial state building a dependency tree.
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a dependency tree
   */
  abstract protected fun processState(
    extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
    beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                         context: InputContextType) -> Unit)?): DependencyTree

  /**
   * Generate the possible actions allowed in a given state, assigns them a score and returns them in descending order
   * according to the score.
   *
   * @param scoringGlobalSupportStructure the scoring support structure
   * @param extendedState the [ExtendedState] containing items, context and state
   *
   * @return a list of scored actions, sorted by descending score and then by transition priority
   */
  protected fun getScoredActions(
    scoringGlobalSupportStructure: ScoringGlobalStructureType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>
  ): List<Transition<TransitionType, StateType>.Action> {

    val decodingContext = DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>(
      actions = this.actionsGenerator.generateFrom(
        transitions = this.transitionSystem.generateTransitions(extendedState.state)),
      extendedState = extendedState)

    this.featuresExtractor.setFeatures(decodingContext = decodingContext, supportStructure = scoringGlobalSupportStructure)

    this.actionsScorer.score(decodingContext = decodingContext, supportStructure = scoringGlobalSupportStructure)

    return decodingContext.sortedActions
  }
}
