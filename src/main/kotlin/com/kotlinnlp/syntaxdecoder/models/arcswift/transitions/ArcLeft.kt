/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.arcswift.transitions

import com.kotlinnlp.syntaxdecoder.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.models.arcswift.ArcSwiftTransition

/**
 * The ArcLeft transition.
 *
 * (σ|ik| . . . |i1, j|β, A) ⇒ (σ, j|β, A ∪ {(j → ik)})
 *
 * @property state the [State] on which this transition operates
 * @property dependentStackIndex the position in the stack of the dependent element
 */
class ArcLeft(state: StackBufferState, val dependentStackIndex: Int) : ArcSwiftTransition(state), SyntacticDependency {

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
  override val governorId: Int get() = this.state.buffer.first()

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.stack[this.dependentStackIndex]

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.buffer.isNotEmpty() && this.dependentStackIndex <= this.state.stack.lastIndex &&
      this.state.dependencyTree.isUnattached(this.state.stack[this.dependentStackIndex])

  /**
   * Ensures that the value of 'dependentStackIndex' is within the limits.
   */
  init { require(this.dependentStackIndex >= 0) }

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.stack = ArrayList(this.state.stack.slice(this.dependentStackIndex + 1 .. this.state.stack.lastIndex))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left(${this.dependentStackIndex})"
}