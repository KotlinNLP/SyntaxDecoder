/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.easyfirst.transitions

import com.kotlinnlp.transitionsystems.PendingListState
import com.kotlinnlp.transitionsystems.State
import com.kotlinnlp.transitionsystems.easyfirst.EasyFirstTransition

/**
 * The ArcRight transition.
 *
 * @property state the [State] on which this transition operates
 */
class ArcRight(state: PendingListState, val i: Int) : EasyFirstTransition(state) {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_RIGHT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 2

  /**
   * The governor id.
   */
  override val governorId: Int get() = state.pendingList[this.i]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = state.pendingList[this.i + 1]

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.i in 0 .. this.state.pendingList.size

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  override fun perform(state: PendingListState) {
    state.pendingList.removeAt(this.i + 1) // remove the dependent
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-right(${this.i})"
}
