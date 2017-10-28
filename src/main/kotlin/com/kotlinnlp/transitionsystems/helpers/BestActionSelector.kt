/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers

import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * BestActionSelector.
 */
interface BestActionSelector<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>> {

  /**
   * @param actions a list of Actions
   * @param extendedState the extended state of the last scored actions
   *
   * @return the best action among the given [actions]
   */
  fun select(
    actions: List<Transition<TransitionType, StateType>.Action>,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ): Transition<TransitionType, StateType>.Action
}
