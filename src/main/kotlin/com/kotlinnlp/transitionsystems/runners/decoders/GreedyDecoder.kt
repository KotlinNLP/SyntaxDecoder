/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.runners.decoders

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.TransitionSystem
import com.kotlinnlp.transitionsystems.helpers.ActionsGenerator
import com.kotlinnlp.transitionsystems.helpers.BestActionSelector
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.ActionsScorer
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.ActionsScorerStructure
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.FeaturesExtractor
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.FeaturesExtractorStructure
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.ItemsFactory
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.syntax.DependencyTree

/**
 * The GreedyDecoder decodes the syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * The transition applied to a state is chosen with a greedy approach, looking for the best local one.
 *
 * @property transitionSystem a [TransitionSystem]
 */
class GreedyDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>,
  FeaturesExtractorStructureType: FeaturesExtractorStructure<
    FeaturesExtractorStructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>,
  ActionsScorerStructureType: ActionsScorerStructure<
    ActionsScorerStructureType, StateType, TransitionType, ContextType, ItemType>>
(
  transitionSystem: TransitionSystem<StateType, TransitionType>,
  itemsFactory: ItemsFactory<ItemType>,
  actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  featuresExtractor: FeaturesExtractor<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
  actionsScorer: ActionsScorer<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, ActionsScorerStructureType>,
  bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>
) :
  SyntaxDecoder<StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType,
    FeaturesExtractorStructureType, ActionsScorerStructureType>(
    transitionSystem = transitionSystem,
    itemsFactory = itemsFactory,
    actionsGenerator = actionsGenerator,
    featuresExtractor = featuresExtractor,
    actionsScorer = actionsScorer,
    bestActionSelector = bestActionSelector
  ) {

  /**
   * The support structure of the [featuresExtractor].
   */
  private val featuresExtractorStructure = featuresExtractor.supportStructureFactory()

  /**
   * The support structure of the [actionsScorer].
   */
  private val actionsScorerStructure = actionsScorer.supportStructureFactory()

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
    beforeApplyAction: ((
      action: Transition<TransitionType, StateType>.Action,
      extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)?): DependencyTree {

    while (!extendedState.state.isTerminal) {

      val bestAction: Transition<TransitionType, StateType>.Action = this.getBestAction(
        featuresExtractorStructure = this.featuresExtractorStructure,
        actionsScorerStructure = this.actionsScorerStructure,
        extendedState = extendedState)

      beforeApplyAction?.invoke(bestAction, extendedState) // external callback

      bestAction.apply()
    }

    return extendedState.state.dependencyTree
  }
}
