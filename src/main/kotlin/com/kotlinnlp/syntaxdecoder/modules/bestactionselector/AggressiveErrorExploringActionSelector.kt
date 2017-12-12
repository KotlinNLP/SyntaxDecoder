/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.bestactionselector

import com.kotlinnlp.syntaxdecoder.context.InputContext
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
  InputContextType : InputContext<InputContextType, ItemType>>
  :
  BestActionSelector<StateType, TransitionType, ItemType, InputContextType> {

  /**
   * @param sortedActions a list of scored actions, sorted by descending score and then by transition priority
   * @param extendedState the extended state of the last scored actions
   *
   * @return the best action among the given [sortedActions]
   */
  override fun select(
    sortedActions: List<Transition<TransitionType, StateType>.Action>,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>
  ): Transition<TransitionType, StateType>.Action {

    val oracle: Oracle<StateType, TransitionType> = checkNotNull(extendedState.oracle)

    val highestScoringAction = sortedActions.first()

    return if (oracle.hasZeroCost(highestScoringAction)) {

      val highestScoringIncorrectAction = sortedActions.find { !oracle.hasZeroCost(it) }

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
