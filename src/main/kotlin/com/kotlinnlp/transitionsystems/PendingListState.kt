/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

/**
 * The PendingListState.
 *
 * @property tokens the list of tokens used to initialize the state.
 */
class PendingListState(tokens: List<Int>) : State<PendingListState>(tokens) {

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
  init { this.tokens.mapTo(this.pendingList, { it }) }

  /**
   * @return a new copy of this [State]
   */
  override fun clone(): PendingListState {

    val clonedState = PendingListState(this.tokens)

    clonedState.dependencyTree = this.dependencyTree.clone()
    clonedState.pendingList = ArrayList(this.pendingList)

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = this.pendingList.toString()
}
