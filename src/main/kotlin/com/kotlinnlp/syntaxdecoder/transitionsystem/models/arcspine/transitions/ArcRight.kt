/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions

import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineState
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineTransition
import com.kotlinnlp.syntaxdecoder.utils.pop
import com.kotlinnlp.syntaxdecoder.utils.secondToLast

/**
 * The ArcRight transition.
 *
 * @property state the [State] on which this transition operates.
 * @property governorSpineIndex the index of the governor within the right spine of the second topmost element in the
 *                              stack
 */
class ArcRight(state: ArcSpineState, val governorSpineIndex: Int) : ArcSpineTransition(state), SyntacticDependency {

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
  override val governorId: Int get() = this.state.stack.secondToLast().rightSpine[this.governorSpineIndex]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = state.stack.last().root

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.state.stack.size > 1 && this.state.stack.secondToLast().rightSpine.size >= this.governorSpineIndex

  /**
   * Ensures that the value of 'governorSpineIndex' is within the limits.
   */
  init { require(this.governorSpineIndex >= 0) }

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    val s0: ArcSpineState.StackElement = this.state.stack.pop()
    val s1: ArcSpineState.StackElement = this.state.stack.pop()

    this.state.stack.add(s1.addToRightSpine(this.governorSpineIndex, s0.rightSpine))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-right(${this.governorSpineIndex})"
}
