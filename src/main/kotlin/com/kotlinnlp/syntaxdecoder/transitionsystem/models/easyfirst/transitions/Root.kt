/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.transitions

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.PendingListState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.EasyFirstTransition

/**
 * The Root transition.
 *
 * @property refState the [State] on which this transition operates
 * @property id the transition id
 */
class Root(refState: PendingListState, id: Int) : EasyFirstTransition(refState, id) {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ROOT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 0

  /**
   * The index of the focus element in the pending list.
   */
  override val pendingListFocus: Int = 0

  /**
   * The governor id.
   */
  override val governorId: Int? = null

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.pendingList.last()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.refState.pendingList.size == 1

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: PendingListState) {
    state.pendingList.clear()
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "root"
}
