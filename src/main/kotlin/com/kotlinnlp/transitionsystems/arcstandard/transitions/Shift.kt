/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcstandard.transitions

import com.kotlinnlp.transitionsystems.StackBufferState
import com.kotlinnlp.transitionsystems.arcstandard.ArcStandardTransition
import com.kotlinnlp.transitionsystems.utils.removeFirst

/**
 * The Shift transition.
 *
 * SHIFT[(σ, [i|β], A)] ⇒ ([σ|i], β, A)
 *
 * @property state the [State] on which this transition operates.
 */
class Shift(state: StackBufferState) : ArcStandardTransition(state) {

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 1

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.state.buffer.isNotEmpty()

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  override fun perform(state: StackBufferState) {
    state.stack.add(state.buffer.removeFirst())
  }

  /**
   * @return a new Action tied to this transition.
   */
  override fun buildAction(id: Int, score: Double): Action = this.buildShift(id = id, score = score)

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "shift"
}