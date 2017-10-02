/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcspine.transitions

import com.kotlinnlp.transitionsystems.Transition.Action
import com.kotlinnlp.transitionsystems.arcspine.ArcSpineState
import com.kotlinnlp.transitionsystems.arcspine.ArcSpineTransition
import com.kotlinnlp.transitionsystems.utils.removeFirst

/**
 * The Shift transition.
 *
 * @property state the [State] on which this transition operates.
 */
class Shift(state: ArcSpineState) : ArcSpineTransition(state) {

  /**
   * The Transition type, from which depends the building of the related [Action].
   */
  override val type: Type = Type.SHIFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 1

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.state.buffer.isNotEmpty()

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  override fun perform(state: ArcSpineState) {
    state.stack.add(ArcSpineState.StackElement(state.buffer.removeFirst()))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "shift"
}
