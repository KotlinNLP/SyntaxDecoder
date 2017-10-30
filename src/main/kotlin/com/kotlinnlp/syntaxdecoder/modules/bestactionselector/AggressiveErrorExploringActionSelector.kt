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
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * This [BestActionSelector] selects the highest scoring incorrect transitions also if they score below correct
 * transitions. Specifically, when the score of the correct transition is greater than that of the wrong transition
 * but the difference is smaller than a margin constant, it selects the incorrect action with a probability of 0.1.
 */
class AggressiveErrorExploringActionSelector<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>>
  :
  BestActionSelector<StateType, TransitionType, ItemType, ContextType> {

  /**
   * @param actions a list of Actions sorted by descending order
   * @param extendedState the extended state of the last scored actions
   *
   * @return the best action among the given [actions]
   */
  override fun select(
    actions: List<Transition<TransitionType, StateType>.Action>,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ): Transition<TransitionType, StateType>.Action {

    val oracle: Oracle<StateType, TransitionType> = checkNotNull(extendedState.oracle)

    val highestScoringAction = actions.first()

    return if (oracle.hasZeroCost(highestScoringAction)) {

      val highestScoringIncorrectAction = actions.find { !oracle.hasZeroCost(it) }

      if (highestScoringIncorrectAction == null) {
        highestScoringAction
      } else if (highestScoringAction.score - highestScoringIncorrectAction.score > 1.0 || Math.random() > 0.1) {
        highestScoringAction
      } else {
        highestScoringIncorrectAction
      }

    } else {
      highestScoringAction
    }
  }
}
