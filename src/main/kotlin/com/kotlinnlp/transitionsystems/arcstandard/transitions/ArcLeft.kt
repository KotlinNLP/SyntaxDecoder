/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcstandard.transitions

import com.kotlinnlp.transitionsystems.StackBufferState
import com.kotlinnlp.transitionsystems.State
import com.kotlinnlp.transitionsystems.SyntacticDependency
import com.kotlinnlp.transitionsystems.arcstandard.ArcStandardTransition
import com.kotlinnlp.transitionsystems.utils.secondToLast

/**
 * The ArcLeft transition.
 *
 * ([σ|i|j], B, A) ⇒ ([σ|j], B, A∪{(j, l, i)})
 *
 * @property state the [State] on which this transition operates.
 */
class ArcLeft(state: StackBufferState) : ArcStandardTransition(state), SyntacticDependency {

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 2

  /**
   * The governor id.
   */
  override val governorId: Int get() = state.stack.last()

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.stack.secondToLast()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.state.stack.size > 1

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  override fun perform(state: StackBufferState) {
    state.stack.removeAt(state.stack.lastIndex - 1) // remove the dependent
  }

  /**
   * @return a new Action tied to this transition.
   */
  override fun buildAction(id: Int, score: Double): Action = this.buildArc(id = id, score = score)

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left"
}