/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwiftTransition

/**
 * The Root transition.
 *
 * Root[(σ|s0, β, T)] ⇒ (σ, β, T ∪ {(s0, root)})
 *
 * @property state the [State] on which this transition operates.
 */
class Root(state: StackBufferState): ArcSwiftTransition(state), SyntacticDependency {

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
  override val governorId: Int? = null

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.state.stack.last()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.buffer.isEmpty() && this.state.stack.isNotEmpty() &&
      this.state.unattachedStackElements.size == 1

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.stack.clear()
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "root"

  /**
   * @return a list of unattached element on the stack.
   */
  private val StackBufferState.unattachedStackElements: List<Int> get() =
    this.stack.filter { this.dependencyTree.isUnattached(it) }
}
