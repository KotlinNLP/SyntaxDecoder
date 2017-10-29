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
import com.kotlinnlp.syntaxdecoder.utils.removeFirst

/**
 * The ArcRight transition.
 *
 * (σ|ik| . . . |i1, j|β, A) ⇒ (σ|ik|j, β, A ∪ {(ik → j)})
 *
 * @property state the [State] on which this transition operates
 * @property governorStackIndex the position in the stack of the governor element
 */
class ArcRight(state: StackBufferState, val governorStackIndex: Int): ArcSwiftTransition(state), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_RIGHT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 2

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.state.stack[this.governorStackIndex]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.buffer.first()

  /**
   * True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get () =
    this.state.buffer.isNotEmpty()
      && this.governorStackIndex <= this.state.stack.lastIndex
      && (this.governorStackIndex == 0 || this.attachedElementsUntilK)

  /**
   * True if all the elements until [governorStackIndex] are already attached.
   */
  private val attachedElementsUntilK: Boolean get() =
    this.state.stack.subList(0, this.governorStackIndex - 1).all { this.state.dependencyTree.isAttached(it) }

  /**
   * Ensures that the value of 'governorStackIndex' is within the limits.
   */
  init { require(this.governorStackIndex >= 0) }

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.stack = ArrayList(this.state.stack.slice(this.governorStackIndex.. this.state.stack.lastIndex))
    this.state.stack.add(0, this.state.buffer.removeFirst())
  }

  /**
   * @return the string representation of this transition
   */
  override fun toString(): String = "arc-right(${this.governorStackIndex})"
}
