/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.arcspine.transitions

import com.kotlinnlp.syntaxdecoder.models.arcspine.ArcSpineState
import com.kotlinnlp.syntaxdecoder.models.arcspine.ArcSpineTransition
import com.kotlinnlp.syntaxdecoder.utils.removeFirst

/**
 * The Shift transition.
 *
 * @property state the [State] on which this transition operates.
 */
class Shift(state: ArcSpineState) : ArcSpineTransition(state) {

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
  override val isAllowed: Boolean get() = this.state.buffer.isNotEmpty()

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.stack.add(ArcSpineState.StackElement(this.state.buffer.removeFirst()))
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "shift"
}
