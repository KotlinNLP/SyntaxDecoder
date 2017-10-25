/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.resetErrors
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
  in ExtendedStateType : ExtendedState<StateType, TransitionType, ItemType, ContextType>> {

  /**
   * Whether last assigned errors have been considered relevant.
   */
  var areErrorsRelevant: Boolean = false
    protected set

  /**
   * Assign errors to the given [actions] using the given [extendedState] as context.
   *
   * @param actions a list with the last scored actions
   * @param extendedState the extended state of the last scored actions
   */
  fun setErrors(actions: List<Transition<TransitionType, StateType>.Action>,
                extendedState: ExtendedStateType){

    actions.resetErrors()

    this.areErrorsRelevant = false

    this.assignErrors(actions = actions, extendedState = extendedState)
  }

  /**
   * Assign errors to the given [actions] using the given [extendedState] as context.
   *
   * @param actions a list with the last scored actions
   * @param extendedState the extended state of the last scored actions
   */
  abstract protected fun assignErrors(actions: List<Transition<TransitionType, StateType>.Action>,
                                      extendedState: ExtendedStateType)
}
