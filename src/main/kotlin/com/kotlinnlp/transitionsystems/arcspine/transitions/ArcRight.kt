/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcspine.transitions

import com.kotlinnlp.transitionsystems.SyntacticDependency
import com.kotlinnlp.transitionsystems.Transition.Action
import com.kotlinnlp.transitionsystems.arcspine.ArcSpineState
import com.kotlinnlp.transitionsystems.arcspine.ArcSpineTransition
import com.kotlinnlp.transitionsystems.utils.pop
import com.kotlinnlp.transitionsystems.utils.secondToLast

/**
 * The ArcRight transition.
 *
 * @property state the [State] on which this transition operates.
 * @property k the position of the k-th node in the right spine of the topmost element in the stack.
 */
class ArcRight(state: ArcSpineState, val k: Int) : ArcSpineTransition(state), SyntacticDependency{

  /**
   * The Transition type, from which depends the building of the related [Action].
   */
  override val type: Type = Type.ARC_RIGHT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 3

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.state.stack.secondToLast().rightSpine[this.k]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = state.stack.last().root

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.stack.size > 1 && this.state.stack.secondToLast().rightSpine.size >= this.k

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
  override fun perform(state: ArcSpineState) {
    val s0: ArcSpineState.StackElement = this.state.stack.pop()
    val s1: ArcSpineState.StackElement = this.state.stack.pop()

    state.stack.add(s1.addToRightSpine(this.k, s0.rightSpine))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-right(${this.k})"
}
