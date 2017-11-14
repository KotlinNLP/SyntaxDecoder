/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.attardi.transitions

import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.attardi.AttardiTransition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.utils.extractAndRemove
import com.kotlinnlp.syntaxdecoder.utils.removeLast

/**
 * The ArcLeft transition.
 *
 * L-1l  (σ|i, j|β, A)  ⇒  (σ, j|β, A ∪ {(j, l, i)})
 * L-2l  (σ|i|j, k|β, A)  ⇒  (σ|j, k|β, A ∪ {(k, l, i)})
 * L-3l  (σ|i|j|k, m|β, A)  ⇒  (σ, j|k|m|β, A ∪ {(m, l, i)})
 *
 * @property refState the [State] on which this transition operates
 * @property degree the position in the stack of the dependent element
 * @property id the transition id
 */
class ArcLeft(
  refState: StackBufferState,
  val degree: Int,
  id: Int
) : AttardiTransition(refState, id), SyntacticDependency {

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
  override val dependentId: Int get() = this.refState.stack[this.stackSize - 1 - this.degree]

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.refState.buffer.isNotEmpty() && this.stackSize >= this.degree

  /**
   * The size of the Stack.
   */
  private val stackSize: Int = this.refState.stack.size

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: StackBufferState) {

    when {

      this.degree == 0 ->
        state.stack.removeLast()

      this.degree == 1 ->
        state.stack.removeAt(this.refState.stack.lastIndex - 1)

      else -> {
        state.buffer.addAll(0, state.stack.extractAndRemove(this.stackSize - this.degree .. this.refState.stack.lastIndex))
        state.stack.removeLast()
      }
    }
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left(${this.degree})"
}
