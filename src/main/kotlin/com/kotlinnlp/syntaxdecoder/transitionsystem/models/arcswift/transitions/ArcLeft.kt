/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwiftTransition

/**
 * The ArcLeft transition.
 *
 * (σ|ik| . . . |i1, j|β, A) ⇒ (σ, j|β, A ∪ {(j → ik)})
 *
 * @property refState the [State] on which this transition operates
 * @property dependentStackIndex the position in the stack of the dependent element
 * @property id the transition id
 */
class ArcLeft(
  refState: StackBufferState,
  val dependentStackIndex: Int,
  id: Int
) : ArcSwiftTransition(refState, id), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_LEFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 2

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.refState.buffer.first()

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.stack[this.dependentStackIndex]

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.refState.buffer.isNotEmpty() && this.dependentStackIndex <= this.refState.stack.lastIndex &&
      this.refState.dependencyTree.isUnattached(this.refState.stack[this.dependentStackIndex])

  /**
   * Ensures that the value of 'dependentStackIndex' is within the limits.
   */
  init { require(this.dependentStackIndex >= 0) }

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: StackBufferState) {
    state.stack = ArrayList(state.stack.slice(this.dependentStackIndex + 1 .. state.stack.lastIndex))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left(${this.dependentStackIndex})"
}
