/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.bestactionselector

import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * A multiple actions selector that selects actions by score: an action is selected if the future state logScore,
 * obtained applying it, is greater then or equal to a given threshold.
 */
class MultliActionsSelectorByScore<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  InputContextType : InputContext<InputContextType, ItemType>>
  : MultiActionsSelector<StateType, TransitionType, ItemType, InputContextType> {

  /**
   * @param sortedActions a list of scored actions, sorted by descending score and then by transition priority
   * @param extendedState the extended state of the last scored actions
   * @param scoreThreshold the minimum score threshold (must be <= 0.0 or null for -inf)
   *
   * @return the list of best actions among the given [sortedActions]
   */
  override fun select(sortedActions: List<Transition<TransitionType, StateType>.Action>,
                      extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
                      scoreThreshold: Double?): List<Transition<TransitionType, StateType>.Action> {

    assert(scoreThreshold == null || scoreThreshold <= 0.0) {
      "The score threshold must be <= 0.0 (given $scoreThreshold)."
    }

    return sortedActions.filter { it.isAllowed  }.takeWhile {
      scoreThreshold == null || extendedState.estimateFutureScore(it) >= scoreThreshold
    }
  }
}
