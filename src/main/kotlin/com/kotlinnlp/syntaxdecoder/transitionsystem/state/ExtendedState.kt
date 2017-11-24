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
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.scoreaccumulator.ScoreAccumulator

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
  val scoreAccumulator: ScoreAccumulator<*>,
  val oracle: Oracle<StateType, TransitionType>? = null) {

  /**
   * The score of goodness of this state.
   */
  val score: Double get() = this.scoreAccumulator.getCurrent()

  /**
   * Accumulate the given [addingScore] into this state .
   *
   * @param addingScore the adding score
   */
  fun accumulateScore(addingScore: Double) = this.scoreAccumulator.accumulate(addingScore)

  /**
   * Simulate the application of a given [action] to the [state], returning the estimated future score.
   *
   * @param action an action to apply to the [state]
   *
   * @return the score that this state will assume if applying the given [action] to its [state]
   */
  fun estimateFutureScore(action: Transition<TransitionType, StateType>.Action): Double
    = this.scoreAccumulator.estimateAccumulation(action.score)

  /**
   * @param state the new state that will replace the current one in the clone
   *
   * @return a clone of this [ExtendedState] replacing its state with the given [state]
   */
  fun clone(state: StateType): ExtendedState<StateType, TransitionType, ItemType, ContextType> = ExtendedState(
    state = state,
    context = this.context.copy(),
    oracle = this.oracle?.copy(),
    scoreAccumulator = this.scoreAccumulator.copy())
}
