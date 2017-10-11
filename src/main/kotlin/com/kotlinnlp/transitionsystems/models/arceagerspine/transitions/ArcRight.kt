/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arceagerspine.transitions

import com.kotlinnlp.transitionsystems.State
import com.kotlinnlp.transitionsystems.SyntacticDependency
import com.kotlinnlp.transitionsystems.models.arceagerspine.ArcEagerSpineState
import com.kotlinnlp.transitionsystems.models.arceagerspine.ArcEagerSpineTransition
import com.kotlinnlp.transitionsystems.utils.pop
import com.kotlinnlp.transitionsystems.utils.removeFirst

/**
 * The ArcRight transition.
 *
 * @property state the [State] on which this transition operates.
 * @property k the position of the k-th node in the right spine of the topmost element in the stack.
 */
class ArcRight(state: ArcEagerSpineState, val k: Int) : ArcEagerSpineTransition(state), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_LEFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 3

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.state.stack.last()[this.k]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = state.buffer.first()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.buffer.isNotEmpty() &&
      this.state.stack.isNotEmpty() &&
      this.state.stack.last().size >= this.k

  /**
   * Ensures that the value of 'k' is within the limits.
   */
  init { require(this.k >= 0) }

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {

    this.state.stack.last().insert(this.k + 1, this.state.buffer.removeFirst())

    if (this.state.stack.size > 1 && this.state.buffer.isEmpty()) this.unshift()
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-right(${this.k})"

  /**
   * Perform an unshift on its [state].
   */
  private fun unshift() = this.state.buffer.add(this.state.stack.pop().root)
}
