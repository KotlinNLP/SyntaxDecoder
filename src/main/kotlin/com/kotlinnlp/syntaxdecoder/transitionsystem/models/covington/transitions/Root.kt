/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions

import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonState
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonTransition

/**
 * The Root transition.
 *
 * (λ1|i, [], [], A) ⇒ ([], [], [], A ∪ {null → i})
 *
 * @property refState the [State] on which this transition operates.
 */
class Root(refState: CovingtonState) : CovingtonTransition(refState), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ROOT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 0

  /**
   * The governor id.
   */
  override val governorId: Int? get() = null

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.stack1.last()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.refState.buffer.isEmpty() && this.refState.stack2.isEmpty()
    && this.refState.stack1.size == 1

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: CovingtonState) {
    state.stack1.clear()
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "root"
}
