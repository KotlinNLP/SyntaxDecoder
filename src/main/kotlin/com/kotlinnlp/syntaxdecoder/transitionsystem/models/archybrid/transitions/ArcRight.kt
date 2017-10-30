/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybridTransition
import com.kotlinnlp.syntaxdecoder.utils.pop
import com.kotlinnlp.syntaxdecoder.utils.secondToLast

/**
 * The ArcRight transition.
 *
 * ArcRight[(σ|s1|s0, β, T)] ⇒ (σ|s1, β, T ∪ {(s1, s0)})
 *
 * @property refState the [State] on which this transition operates.
 */
class ArcRight(refState: StackBufferState) : ArcHybridTransition(refState), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_RIGHT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 3

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.refState.stack.secondToLast()

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.stack.last()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.refState.stack.size > 1

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: StackBufferState) {
    state.stack.pop() // remove the dependent
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-right"
}
