/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The StackBufferState.
 *
 * @property itemIds the list of item ids used to initialize the state
 * @property size the size of the sentence used to initialize the state
 */
class StackBufferState(
  itemIds: List<Int>,
  size: Int
) : State<StackBufferState>(itemIds, size) {

  /**
   * The buffer.
   */
  var buffer = ArrayList<Int>()

  /**
   * The stack.
   */
  var stack = ArrayList<Int>()

  /**
   * True when the state reach the end.
   */
  override val isTerminal get() = this.stack.isEmpty() && this.buffer.isEmpty()

  /**
   * Initialize the state.
   */
  init { this.itemIds.mapTo(this.buffer, { it }) }

  /**
   * @return a new copy of this [State]
   */
  override fun copy(): StackBufferState {

    val clonedState = StackBufferState(this.itemIds, this.size)

    clonedState.dependencyTree = this.dependencyTree.clone()
    clonedState.buffer = ArrayList(this.buffer)
    clonedState.stack = ArrayList(this.stack)

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = "S ${this.stack} B ${this.buffer}"
}
