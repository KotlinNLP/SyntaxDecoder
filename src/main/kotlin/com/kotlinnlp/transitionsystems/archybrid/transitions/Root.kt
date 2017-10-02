/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.archybrid.transitions

import com.kotlinnlp.transitionsystems.StackBufferState
import com.kotlinnlp.transitionsystems.State
import com.kotlinnlp.transitionsystems.SyntacticDependency
import com.kotlinnlp.transitionsystems.Transition.Action
import com.kotlinnlp.transitionsystems.archybrid.ArcHybridTransition
import com.kotlinnlp.transitionsystems.utils.pop

/**
 * The Root transition.
 *
 * Root[(σ|s0, β, T)] ⇒ (σ, β, T ∪ {(s0, root)})
 *
 * @property state the [State] on which this transition operates.
 */
class Root(state: StackBufferState) : ArcHybridTransition(state), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related [Action].
   */
  override val type: Type = Type.ROOT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 0

  /**
   * The governor id.
   */
  override val governorId: Int? = null // root

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.stack.last()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.state.stack.size == 1 && this.state.buffer.isEmpty()

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  override fun perform(state: StackBufferState) {
    state.stack.pop()
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "root"
}
