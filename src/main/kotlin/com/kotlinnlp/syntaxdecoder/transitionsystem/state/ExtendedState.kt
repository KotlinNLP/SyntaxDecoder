/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state

import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.utils.Clonable

/**
 * The [ExtendedState] extends a [State] with the list of [StateItem]s that compose it and a [DecodingContext].
 *
 * This structure allows you to keep aligned with state the properties that can evolve together with it.
 *
 * @property state a [State]
 * @property context a [DecodingContext]
 * @property oracle an [Oracle] (optional)
 */
data class ExtendedState<
  StateType : State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>>(
  val state: StateType,
  val context: ContextType,
  val oracle: Oracle<StateType, TransitionType>? = null
) : Clonable<ExtendedState<StateType, TransitionType, ItemType, ContextType>> {

  /**
   * The score of goodness of this state (a value in the range (0.0, 1.0]), default 1.0.
   */
  val score: Double get() = 1 / (-0.1 * this._score + 1) // convert the domain (-inf, 0.0] to (0.0, 1.0]

  /**
   * The score of goodness of this state (a value in the range (-inf, 0.0]), default 0.0.
   * It is the result of more additions of the logarithm of scores in the range (0.0, 1.0], done calling the
   * [accumulateScore] method.
   */
  private var _score: Double = 0.0

  /**
   * Accumulate the given [score] into this state as joint probability of its score (after have transformed it by
   * natural logarithm, to avoid underflow).
   *
   * @param score a score in the range (0.0, 1.0]
   */
  fun accumulateScore(score: Double) {
    assert(score > 0 && score in 0.0 .. 1.0) { "Invalid score: $score, must be in range (0.0, 1.0]." }
    this._score += Math.log(score)
  }

  /**
   * @return a copy of this [ExtendedState]
   */
  override fun copy(): ExtendedState<StateType, TransitionType, ItemType, ContextType> {

    val clonedState = ExtendedState(
      state = this.state.copy(),
      context = this.context.copy(),
      oracle = this.oracle?.copy())

    clonedState._score = this._score

    return clonedState
  }
}
