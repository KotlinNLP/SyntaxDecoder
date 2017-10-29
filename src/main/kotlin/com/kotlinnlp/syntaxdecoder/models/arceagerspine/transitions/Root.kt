/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.arceagerspine.transitions

import com.kotlinnlp.syntaxdecoder.state.State
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.models.arceagerspine.ArcEagerSpineState
import com.kotlinnlp.syntaxdecoder.models.arceagerspine.ArcEagerSpineTransition
import com.kotlinnlp.syntaxdecoder.utils.pop

/**
 * The Root transition.
 *
 * Root[(σ|s0, β, T)] ⇒ (σ, β, T ∪ {(s0, root)})
 *
 * @property state the [State] on which this transition operates.
 */
class Root(state: ArcEagerSpineState) : ArcEagerSpineTransition(state), SyntacticDependency {

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
  override val governorId: Int? = null // root

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.stack.last().root

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.stack.size == 1 && this.state.buffer.isEmpty()

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.stack.pop()
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "root"
}
