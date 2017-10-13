/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.syntax.DependencyTree

/**
 * The SyntaxDecoder.
 *
 * Processes the input sentence by means of transitions which incrementally build the dependency tree.
 *
 * The algorithm uses a transition-based system. The system is initialized to an initial state based
 * on the input sentence, to which transitions are applied repeatedly generating new states
 * until the final state is reached.
 *
 * @property transitionSystem a [TransitionSystem]
 */
class SyntaxDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>,
  ExtendedStateType : ExtendedState<ExtendedStateType, StateType, ItemType, ContextType>>
(
  private val transitionSystem: TransitionSystem<
    StateType, TransitionType, StateViewType, ContextType, FeaturesType, ItemType, ExtendedStateType>
) {

  /**
   * Decode the syntax of the given items building a dependency tree.
   *
   * @param itemIds a list of item ids
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a [DependencyTree]
   */
  fun decode(itemIds: List<Int>,
             extendedState: ExtendedStateType,
             beforeApplyAction: (action: Transition<TransitionType, StateType>.Action) -> Unit = {}): DependencyTree {

    val state: StateType = this.transitionSystem.getInitialState(itemIds)

    while (!state.isTerminal) {

      val bestAction: Transition<TransitionType, StateType>.Action
        = this.transitionSystem.getBestAction(state = state, extendedState = extendedState)

      beforeApplyAction(bestAction) // external callback

      bestAction.apply()
    }

    return state.dependencyTree
  }
}
