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
import com.kotlinnlp.syntaxdecoder.utils.removeFirst

/**
 * The ArcRight transition.
 *
 * (σ|ik| . . . |i1, j|β, A) ⇒ (σ|ik|j, β, A ∪ {(ik → j)})
 *
 * @property refState the [State] on which this transition operates
 * @property governorStackIndex the position in the stack of the governor element
 * @property id the transition id
 */
class ArcRight(
  refState: StackBufferState,
  val governorStackIndex: Int,
  id: Int
) : ArcSwiftTransition(refState, id), SyntacticDependency {

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
  override val governorId: Int get() = this.refState.stack[this.governorStackIndex]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.buffer.first()

  /**
   * True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get () =
    this.refState.buffer.isNotEmpty()
      && this.governorStackIndex <= this.refState.stack.lastIndex
      && (this.governorStackIndex == 0 || this.attachedElementsUntilK)

  /**
   * True if all the elements until [governorStackIndex] are already attached.
   */
  private val attachedElementsUntilK: Boolean get() =
    this.refState.stack.subList(0, this.governorStackIndex - 1).all { this.refState.dependencyTree.isAssigned(it) }

  /**
   * Ensures that the value of 'governorStackIndex' is within the limits.
   */
  init { require(this.governorStackIndex >= 0) }

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: StackBufferState) {
    state.stack = state.stack.slice(this.governorStackIndex.. state.stack.lastIndex).toMutableList()
    state.stack.add(0, state.buffer.removeFirst())
  }

  /**
   * @return the string representation of this transition
   */
  override fun toString(): String = "arc-right(${this.governorStackIndex})"
}
