/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state.templates

import com.kotlinnlp.transitionsystems.state.State

/**
 * The PendingListState.
 *
 * @property itemIds the list of item ids used to initialize the state
 */
class PendingListState(itemIds: List<Int>) : State<PendingListState>(itemIds) {

  /**
   * The PendingList.
   */
  var pendingList = ArrayList<Int>()

  /**
   * True when the state reach the end.
   */
  override val isTerminal get() = this.pendingList.isEmpty()

  /**
   * Initialize the state.
   */
  init { this.itemIds.mapTo(this.pendingList, { it }) }

  /**
   * @return a new copy of this [State]
   */
  override fun copy(): PendingListState {

    val clonedState = PendingListState(this.itemIds)

    clonedState.dependencyTree = this.dependencyTree.clone()
    clonedState.pendingList = ArrayList(this.pendingList)

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = this.pendingList.toString()
}