/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcswift.transitions

import com.kotlinnlp.transitionsystems.StackBufferState
import com.kotlinnlp.transitionsystems.SyntacticDependency
import com.kotlinnlp.transitionsystems.arcswift.ArcSwiftTransition

/**
 * The ArcLeft transition.
 *
 * (σ|ik| . . . |i1, j|β, A) ⇒ (σ, j|β, A ∪ {(j → ik)})
 *
 * @property state the [State] on which this transition operates.
 * @property k the position of the k-th node of the stack.
 */
class ArcLeft(state: StackBufferState, val k: Int) : ArcSwiftTransition(state), SyntacticDependency {

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 2

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.state.buffer.first()

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.stack[this.k]

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.buffer.isNotEmpty() && this.k <= this.state.stack.lastIndex &&
      this.state.dependencyTree.isUnattached(this.state.stack[this.k])

  /**
   * Ensures that the value of 'k' is within the limits.
   */
  init { require(this.k >= 0) }

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  override fun perform(state: StackBufferState) {
    state.stack = ArrayList(state.stack.slice(this.k + 1 .. state.stack.lastIndex))
  }

  /**
   * @return a new Action tied to this transition.
   */
  override fun buildAction(id: Int, score: Double): Action = this.buildArc(id = id, score = score)

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left(${this.k})"
}