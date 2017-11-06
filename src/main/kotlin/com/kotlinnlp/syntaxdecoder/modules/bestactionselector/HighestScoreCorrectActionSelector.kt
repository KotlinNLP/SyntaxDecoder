/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.bestactionselector

import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * This [BestActionSelector] selects the first correct highest score action.
 */
class HighestScoreCorrectActionSelector<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>>
  :
  BestActionSelector<StateType, TransitionType, ItemType, ContextType> {

  /**
   * @param sortedActions a list of scored actions, sorted by descending score and then by transition priority
   * @param extendedState the extended state of the last scored actions
   *
   * @return the best action among the given [sortedActions]
   */
  override fun select(
    sortedActions: List<Transition<TransitionType, StateType>.Action>,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ): Transition<TransitionType, StateType>.Action {

    return sortedActions.first { extendedState.oracle!!.isCorrect(it) }
  }
}
