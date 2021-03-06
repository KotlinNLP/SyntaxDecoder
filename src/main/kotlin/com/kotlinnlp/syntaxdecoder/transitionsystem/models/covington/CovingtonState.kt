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
 */
class CovingtonState(itemIds: List<Int>) : State<CovingtonState>(itemIds) {

  /**
   * Contains the words to be processed.
   */
  val buffer: MutableList<Int> = itemIds.toMutableList()

  /**
   * Contains the already processed words for which the parser
   * still has not decided, in the current state, the type of relation
   * with respect to the focus word j, located at the top of Buffer.
   */
  var stack1: MutableList<Int> = mutableListOf()
    private set

  /**
   * Contains the already processed words for which the parser has already
   * determined the type of relation with respect to j in the current step.
   */
  var stack2: MutableList<Int> = mutableListOf()
    private set

  /**
   * True when the state reach the end.
   */
  override val isTerminal get() = this.buffer.isEmpty()

  /**
   * @return a new copy of this [State].
   */
  override fun copy(): CovingtonState {

    val clonedState = CovingtonState(this.itemIds)

    clonedState.dependencyTree = this.dependencyTree.clone()

    clonedState.stack1 = this.stack1.toMutableList()
    clonedState.stack2 = this.stack2.toMutableList()

    return clonedState
  }

  /**
   * @return its string representation.
   */
  override fun toString(): String = "λ1 ${this.stack1} λ2 ${this.stack2} B ${this.buffer}"
}
