/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The PendingListState.
 *
 * @property itemIds the list of item ids used to initialize the state
 */
class PendingListState(itemIds: List<Int>) : State<PendingListState>(itemIds), Focusable {

  /**
   * The pending list.
   */
  val pendingList: MutableList<Int> = itemIds.toMutableList()

  /**
   * True when the state reach the end.
   */
  override val isTerminal get() = this.pendingList.isEmpty()

  /**
   * @return a new copy of this [State]
   */
  override fun copy(): PendingListState {

    val clonedState = PendingListState(this.itemIds)

    clonedState.dependencyTree = this.dependencyTree.clone()

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = this.pendingList.toString()
}
