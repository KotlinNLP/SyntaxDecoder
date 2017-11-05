/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwiftTransition
import com.kotlinnlp.syntaxdecoder.utils.removeFirst


/**
 * The Shift transition.
 *
 * (σ, i|β, A) ⇒ (σ|i, β, A)
 *
 * @property refState the [State] on which this transition operates
 * @property id the transition id
 */
class Shift(refState: StackBufferState, id: Int) : ArcSwiftTransition(refState, id) {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.SHIFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 1

  /**
   * True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.refState.buffer.size > 1 || (this.refState.buffer.size == 1 && this.refState.stack.isEmpty())

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: StackBufferState) {
    state.stack.add(0, state.buffer.removeFirst())
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "shift"
}
