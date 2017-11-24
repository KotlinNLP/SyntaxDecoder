/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.actionserrorssetter

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem

/**
 * The helper that set errors into actions.
 */
abstract class ActionsErrorsSetter<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>> {

  /**
   * Whether last assigned errors have been considered relevant.
   */
  var areErrorsRelevant: Boolean = false
    protected set

  /**
   * Assign errors to the given [sortedActions] using the given [extendedState] as context.
   *
   * @param sortedActions a list with the last scored actions, sorted by score and then by transition priority
   * @param extendedState the extended state of the last scored actions
   */
  fun setErrors(sortedActions: List<Transition<TransitionType, StateType>.Action>,
                extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) {

    this.areErrorsRelevant = false

    this.assignErrors(sortedActions = sortedActions, extendedState = extendedState)
  }

  /**
   * Assign errors to the given [sortedActions] using the given [extendedState] as context.
   *
   * @param sortedActions a list with the last scored actions, sorted by score and then by transition priority
   * @param extendedState the extended state of the last scored actions
   */
  abstract protected fun assignErrors(sortedActions: List<Transition<TransitionType, StateType>.Action>,
                                      extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>)
}
