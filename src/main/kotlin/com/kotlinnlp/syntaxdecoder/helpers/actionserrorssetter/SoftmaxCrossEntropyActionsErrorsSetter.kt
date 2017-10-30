/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.helpers.actionserrorssetter

import com.kotlinnlp.syntaxdecoder.DecodingContext
import com.kotlinnlp.syntaxdecoder.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The [ActionsErrorsSetter] that assigns errors to actions scored using a SoftMax function.
 * Errors are calculated as the derivative of the cross-entropy loss function.
 *
 * @property minRelevantError the margin up to which to set the errors
 */
class SoftmaxCrossEntropyActionsErrorsSetter<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>>
(
  private val minRelevantError: Double = 10e-03
) :
  ActionsErrorsSetter<
    StateType,
    TransitionType,
    ItemType,
    ContextType>() {

  /**
   * Assign errors to the given [actions] using the given [extendedState] as context.
   *
   * @param actions a list with the last scored actions
   * @param extendedState the extended state of the last scored actions
   */
  override fun assignErrors(actions: List<Transition<TransitionType, StateType>.Action>,
                            extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) {

    val oracle: Oracle<StateType, TransitionType> = checkNotNull(extendedState.oracle)

    val highestScoreCorrectAction = actions.first { oracle.isCorrect(it) }

    val loss = 1.0 - highestScoreCorrectAction.score

    if (loss > this.minRelevantError) {

      actions.forEach {
        it.error = if (it.id == highestScoreCorrectAction.id) loss else it.score
      }

      this.areErrorsRelevant = true
    }
  }
}
