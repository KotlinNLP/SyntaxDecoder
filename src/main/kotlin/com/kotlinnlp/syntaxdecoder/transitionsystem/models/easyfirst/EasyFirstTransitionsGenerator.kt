/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.PendingListState
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.transitions.ArcLeft
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.transitions.ArcRight
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.transitions.Root

/**
 * The TransitionsGenerator for the EasyFirst Transition System.
 */
class EasyFirstTransitionsGenerator : TransitionsGenerator<PendingListState, EasyFirstTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: PendingListState): List<EasyFirstTransition>  {

    val transitions: MutableList<EasyFirstTransition> = mutableListOf()

    transitions.add(Root(state, id = transitions.getNextId()))
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add multiple arc-left and arc-right transitions.
   */
  private fun MutableList<EasyFirstTransition>.addArcs(state: PendingListState) {

    (0 until state.pendingList.lastIndex).forEach { i ->
      this.add(ArcLeft(state, pendingListFocus = i, id = this.getNextId()))
      this.add(ArcRight(state, pendingListFocus = i, id = this.getNextId()))
    }
  }
}
