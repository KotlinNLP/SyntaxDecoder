/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineState
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineTransition
import com.kotlinnlp.syntaxdecoder.utils.pop
import com.kotlinnlp.syntaxdecoder.utils.secondToLast

/**
 * The ArcLeft transition.
 *
 * @property refState the [State] on which this transition operates
 * @property governorSpineIndex the index of the governor within the left spine of the topmost element in the stack
 * @property id the transition id
 */
class ArcLeft(
  refState: ArcSpineState,
  val governorSpineIndex: Int,
  id: Int
) : ArcSpineTransition(refState, id), SyntacticDependency {

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
  override val governorId: Int get() = this.refState.stack.last().leftSpine[this.governorSpineIndex]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.stack.secondToLast().root

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() =
    this.refState.stack.size > 1 && this.refState.stack.last().leftSpine.size >= this.governorSpineIndex

  /**
   * Ensures that the value of 'governorSpineIndex' is within the limits.
   */
  init { require(this.governorSpineIndex >= 0) }

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: ArcSpineState) {
    val s0: ArcSpineState.StackElement = state.stack.pop()
    val s1: ArcSpineState.StackElement = state.stack.pop()

    state.stack.add(s0.addToLeftSpine(this.governorSpineIndex, s1.leftSpine))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left(${this.governorSpineIndex})"
}
