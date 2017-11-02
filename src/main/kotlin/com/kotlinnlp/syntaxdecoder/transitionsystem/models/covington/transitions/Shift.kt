/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonState
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonTransition
import com.kotlinnlp.syntaxdecoder.utils.removeFirst

/**
 * The Shift transition.
 *
 * (λ1, λ2, j|B, A) ⇒ (λ1 · λ2|j, [], B, A)
 *
 * @property refState the [State] on which this transition operates.
 */
class Shift(refState: CovingtonState) : CovingtonTransition(refState) {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.SHIFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 1

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.refState.buffer.isNotEmpty()

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: CovingtonState) {

    if (state.stack2.isNotEmpty()) {
      state.stack1.addAll(state.stack2)
      state.stack2.clear()
    }

    state.stack1.add(state.buffer.removeFirst())
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "shift"
}
