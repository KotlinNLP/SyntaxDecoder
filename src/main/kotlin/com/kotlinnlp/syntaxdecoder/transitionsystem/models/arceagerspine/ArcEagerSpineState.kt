/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.utils.removeFrom

/**
 * The ArcEagerSpine State.
 *
 * @property itemIds the list of item ids used to initialize the state
 */
class ArcEagerSpineState(idemIds: List<Int>) : State<ArcEagerSpineState>(idemIds) {

  /**
   * The StackElement for the ArcEagerSpine.
   *
   * A StackElement contains the right spine.
   *
   * @property root the root of this tree.
   * @constructor insert the [root] as the first right-spines node.
   */
  data class StackElement(val root: Int) : ArrayList<Int>() {

    init { this.add(root) }

    /**
     *
     */
    fun insert (index: Int, element: Int): StackElement {
      if (index > this.lastIndex) this.removeFrom(index)
      this.add(element)
      return this
    }

    /**
     * @return its string representation.
     */
    override fun toString(): String = super.toString()
  }

  /**
   * The buffer.
   */
  var buffer: MutableList<Int> = itemIds.toMutableList()

  /**
   * The stack.
   */
  var stack: MutableList<StackElement> = mutableListOf()

  /**
   * True when the state reach the end.
   */
  override val isTerminal get() = this.stack.isEmpty() && this.buffer.isEmpty()

  /**
   * @return a new copy of this [State]
   */
  override fun copy(): ArcEagerSpineState {

    val clonedState = ArcEagerSpineState(this.itemIds)

    clonedState.dependencyTree = this.dependencyTree.clone()
    clonedState.stack = this.stack.toMutableList()

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = "S ${this.stack} B ${this.buffer}"
}
