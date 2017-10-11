/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arceagerspine.transitions

import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.models.arceagerspine.ArcEagerSpineState
import com.kotlinnlp.transitionsystems.models.arceagerspine.ArcEagerSpineTransition
import com.kotlinnlp.transitionsystems.utils.removeFirst

/**
 * The Shift transition.
 *
 * Shift[(σ, b0|β, T)] ⇒ (σ|b0, β, T)
 * 
 * @property state the [State] on which this transition operates
 */
class Shift(state: ArcEagerSpineState) : ArcEagerSpineTransition(state) {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.SHIFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 1

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.buffer.size > 1 || (this.state.buffer.isNotEmpty() && this.state.stack.isEmpty())

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.stack.add(ArcEagerSpineState.StackElement(this.state.buffer.removeFirst()))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "shift"
}
