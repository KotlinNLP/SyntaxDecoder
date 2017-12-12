/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The Covington State.
 *
 * @property itemIds the list of item ids used to initialize the state
 * @property size the size of the sentence used to initialize the state
 */
class CovingtonState(
  itemIds: List<Int>,
  size: Int
) : State<CovingtonState>(itemIds, size) {

  /**
   * Contains the words to be processed.
   */
  var buffer = ArrayList<Int>()

  /**
   * Contains the already processed words for which the parser
   * still has not decided, in the current state, the type of relation
   * with respect to the focus word j, located at the top of Buffer.
   */
  var stack1 = ArrayList<Int>()

  /**
   * Contains the already processed words for which the parser has already
   * determined the type of relation with respect to j in the current step.
   */
  var stack2 = ArrayList<Int>()

  /**
   * True when the state reach the end.
   */
  override val isTerminal get() = this.buffer.isEmpty()

  /**
   * Initialize the state.
   */
  init { this.itemIds.mapTo(this.buffer, { it }) }

  /**
   * @return a new copy of this [State].
   */
  override fun copy(): CovingtonState {

    val clonedState = CovingtonState(this.itemIds, this.size)

    clonedState.dependencyTree = this.dependencyTree.clone()

    clonedState.buffer = ArrayList(this.buffer)
    clonedState.stack1 = ArrayList(this.stack1)
    clonedState.stack2 = ArrayList(this.stack2)

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = "λ1 ${this.stack1} λ2 ${this.stack2} B ${this.buffer}"
}