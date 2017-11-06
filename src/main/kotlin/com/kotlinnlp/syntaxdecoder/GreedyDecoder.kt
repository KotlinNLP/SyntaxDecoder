/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.BestActionSelector
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorer
import com.kotlinnlp.syntaxdecoder.modules.ScoringSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.syntax.DependencyTree

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
 */
class GreedyDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  ScoringStructureType : ScoringSupportStructure<
    ScoringStructureType, StateType, TransitionType, ContextType, ItemType, FeaturesType>>
(
  transitionSystem: TransitionSystem<StateType, TransitionType>,
  actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  featuresExtractor: FeaturesExtractor<
    StateType, TransitionType, ContextType, ItemType, FeaturesType, ScoringStructureType>,
  actionsScorer: ActionsScorer<
    StateType, TransitionType, ContextType, ItemType, FeaturesType, ScoringStructureType>,
  bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>
) :
  SyntaxDecoder<StateType, TransitionType, ContextType, ItemType, FeaturesType, ScoringStructureType>(
    transitionSystem = transitionSystem,
    actionsGenerator = actionsGenerator,
    featuresExtractor = featuresExtractor,
    actionsScorer = actionsScorer,
    bestActionSelector = bestActionSelector
  ) {

  /**
   * The support structure to score actions and extract features.
   */
  private val supportStructure = actionsScorer.supportStructureFactory()

  /**
   * Decode the syntax starting from an initial state building a dependency tree.
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a dependency tree
   */
  override fun processState(
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
    beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                         context: ContextType) -> Unit)?): DependencyTree {

    while (!extendedState.state.isTerminal) {

      val bestAction: Transition<TransitionType, StateType>.Action = this.getBestAction(
        supportStructure = this.supportStructure,
        extendedState = extendedState)

      beforeApplyAction?.invoke(bestAction, extendedState.context) // external callback

      bestAction.apply()
    }

    return extendedState.state.dependencyTree
  }
}
