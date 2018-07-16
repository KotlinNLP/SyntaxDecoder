/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.BestActionSelector
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorer
import com.kotlinnlp.syntaxdecoder.modules.supportstructure.DecodingSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.supportstructure.SupportStructureFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.scoreaccumulator.ScoreAccumulator

/**
 * The GreedyDecoder decodes the syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * The transition applied to a state is chosen with a greedy approach, looking for the best local one.
 *
 * @property transitionSystem a transition system
 * @property actionsGenerator an actions generator
 * @property featuresExtractor a features extractor
 * @property actionsScorer an actions scorer
 * @property bestActionSelector a best action selector
 * @property supportStructureFactory a support structure factory
 * @property scoreAccumulatorFactory a factory of score accumulators
 */
class GreedyDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  SupportStructureType : DecodingSupportStructure>
(
  transitionSystem: TransitionSystem<StateType, TransitionType>,
  actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  featuresExtractor: FeaturesExtractor<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    SupportStructureType>,
  actionsScorer: ActionsScorer<StateType, TransitionType, InputContextType, ItemType, FeaturesType,
    SupportStructureType>,
  val bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, InputContextType>,
  supportStructureFactory: SupportStructureFactory<SupportStructureType>,
  scoreAccumulatorFactory: ScoreAccumulator.Factory
) :
  SyntaxDecoder<StateType, TransitionType, InputContextType, ItemType, FeaturesType, SupportStructureType>
  (
    transitionSystem = transitionSystem,
    actionsGenerator = actionsGenerator,
    featuresExtractor = featuresExtractor,
    actionsScorer = actionsScorer,
    supportStructureFactory = supportStructureFactory,
    scoreAccumulatorFactory = scoreAccumulatorFactory
  ) {

  /**
   * The decoding support structure to score actions and extract features.
   */
  private val supportStructure = this.supportStructureFactory.globalStructure()

  /**
   * Decode the syntax starting from an initial state building a dependency tree.
   *
   * @param state the [ExtendedState] containing items, context and state
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a dependency tree
   */
  override fun processState(
    state: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
    beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                         context: InputContextType) -> Unit)?): DependencyTree {

    while (!state.isTerminal) {

      val bestAction: Transition<TransitionType, StateType>.Action = this.getBestAction(state)

      beforeApplyAction?.invoke(bestAction, state.context) // external callback

      bestAction.apply()
      state.addAction(bestAction)
    }

    return state.dependencyTree
  }

  /**
   * Get the best action to apply, given the scoring support structure and an [ExtendedState].
   *
   * @param state the [ExtendedState] containing items, context and state
   *
   * @return the best action to apply to the given state
   */
  private fun getBestAction(
    state: ExtendedState<StateType, TransitionType, ItemType, InputContextType>
  ): Transition<TransitionType, StateType>.Action {

    val scoredActions: List<Transition<TransitionType, StateType>.Action> = this.getScoredActions(
      supportStructure = this.supportStructure,
      extendedState = state)

    return this.bestActionSelector.select(sortedActions = scoredActions, extendedState = state)
  }
}
