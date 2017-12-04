/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.attardi

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.attardi.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState

/**
 * The TransitionsGenerator for the Attardi Transition System.
 */
class AttardiTransitionsGenerator : TransitionsGenerator<StackBufferState, AttardiTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: StackBufferState): List<AttardiTransition> {

    val transitions = ArrayList<AttardiTransition>()

    transitions.addRoot(state)
    transitions.addShift(state)
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add Root transition (if allowed)
   */
  private fun ArrayList<AttardiTransition>.addRoot(state: StackBufferState) {
    val root = Root(state, id = this.getNextId())
    if (root.isAllowed) this.add(root)
  }

  /**
   * Add Shift transition (if allowed)
   */
  private fun ArrayList<AttardiTransition>.addShift(state: StackBufferState) {
    val shift = Shift(state, id = this.getNextId())
    if (shift.isAllowed) this.add(shift)
  }

  /**
   * Add multiple ArcLeft and ArcRight transitions.
   */
  private fun ArrayList<AttardiTransition>.addArcs(state: StackBufferState){

    if (state.buffer.isNotEmpty() && state.stack.isNotEmpty()) {

      (0 until minOf(state.stack.size, Attardi.maxTransitionDegree)).reversed().forEach {
        this.add(ArcLeft(state, degree = it, id = this.getNextId()))
        this.add(ArcRight(state, degree = it, id = this.getNextId()))
      }
    }
  }
}
