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

    transitions.add(Root(state, id = transitions.getNextId()))
    transitions.add(Shift(state, id = transitions.getNextId()))
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add multiple ArcLeft and ArcRight transitions.
   */
  private fun ArrayList<AttardiTransition>.addArcs(state: StackBufferState) {

    (0 until Attardi.maxTransitionDegree).reversed().forEach {
      (0 until minOf(state.stack.size, Attardi.maxTransitionDegree)).reversed().forEach {
        this.add(ArcLeft(state, degree = it, id = this.getNextId()))
        this.add(ArcRight(state, degree = it, id = this.getNextId()))
      }
    }
  }
}
