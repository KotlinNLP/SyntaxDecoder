/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Oracle
import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * The helper that set errors into actions.
 */
abstract class ActionsErrorsSetter<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType>,
  ExtendedStateType : ExtendedState<ExtendedStateType, StateType, ItemType, ContextType>>
(
  protected val oracle: Oracle<StateType, TransitionType>
) {

  /**
   * Assign errors to the given [actions] using the given [extendedState] as context.
   *
   * @param actions a list with the last scored actions
   * @param extendedState the extended state of the last scored actions
   */
  abstract fun assignErrors(actions: List<Transition<TransitionType, StateType>.Action>,
                            extendedState: ExtendedStateType)
}
