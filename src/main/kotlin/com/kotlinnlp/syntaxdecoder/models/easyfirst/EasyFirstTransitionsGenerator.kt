/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.easyfirst

import com.kotlinnlp.syntaxdecoder.state.templates.PendingListState
import com.kotlinnlp.syntaxdecoder.helpers.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.models.easyfirst.transitions.ArcLeft
import com.kotlinnlp.syntaxdecoder.models.easyfirst.transitions.ArcRight
import com.kotlinnlp.syntaxdecoder.models.easyfirst.transitions.Root

/**
 * The TransitionsGenerator for the EasyFirst Transition System.
 */
class EasyFirstTransitionsGenerator : TransitionsGenerator<PendingListState, EasyFirstTransition> {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: PendingListState): List<EasyFirstTransition>  {

    val transitions = ArrayList<EasyFirstTransition>()

    transitions.addRoot(state)
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add Root transition (if allowed).
   */
  private fun ArrayList<EasyFirstTransition>.addRoot(state: PendingListState){
    val root = Root(state)
    if (root.isAllowed) this.add(root)
  }

  /**
   * Add multiple arc-left and arc-right transitions.
   */
  private fun ArrayList<EasyFirstTransition>.addArcs(state: PendingListState) {

    for (i in 0 until state.pendingList.lastIndex) {
      this.add(ArcLeft(state, i))
      this.add(ArcRight(state, i))
    }
  }
}
