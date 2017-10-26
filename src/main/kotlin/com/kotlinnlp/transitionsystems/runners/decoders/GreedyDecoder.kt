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
  in StateViewType : StateView<StateType>,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>>
(
  transitionSystem: TransitionSystem<StateType, TransitionType>,
  itemsFactory: ItemsFactory<ItemType>,
  actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  actionsScorer: ActionsScorer<StateType, TransitionType, StateViewType, ContextType, FeaturesType, ItemType>,
  bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>
) :
  SyntaxDecoder<StateType, TransitionType, StateViewType, ContextType, FeaturesType, ItemType>(
    transitionSystem,
    itemsFactory,
    actionsGenerator,
    actionsScorer,
    bestActionSelector
  ) {

  /**
   * @param extendedState the [ExtendedState] containing items, context and state
   */
  override fun processState(
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
    beforeApplyAction: ((
      action: Transition<TransitionType, StateType>.Action,
      extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)?): DependencyTree {

    while (!extendedState.state.isTerminal) {

      val bestAction: Transition<TransitionType, StateType>.Action = this.getBestAction(extendedState)

      beforeApplyAction?.invoke(bestAction, extendedState) // external callback

      bestAction.apply()
    }

    return extendedState.state.dependencyTree
  }
}
