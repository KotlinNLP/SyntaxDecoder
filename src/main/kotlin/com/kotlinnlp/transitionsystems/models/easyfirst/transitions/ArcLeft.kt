/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.easyfirst.transitions

import com.kotlinnlp.transitionsystems.state.PendingListState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.models.easyfirst.EasyFirstTransition

/**
 * The ArcLeft transition.
 *
 * @property state the [State] on which this transition operates
 */
class ArcLeft(state: PendingListState, val i: Int) : EasyFirstTransition(state) {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_LEFT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 1

  /**
   * The governor id.
   */
  override val governorId: Int get() = state.pendingList[this.i + 1]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = state.pendingList[this.i]

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.i in 0 .. this.state.pendingList.size

  /**
   * Apply this transition on its [state].
   * It requires that the transition [isAllowed] on its [state].
   */
  override fun perform() {
    this.state.pendingList.removeAt(this.i) // remove the dependent
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-left(${this.i})"
}
