/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcdistance

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcdistance.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState

/**
 * The TransitionsGenerator for the ArcDistance Transition System.
 */
class ArcDistanceTransitionsGenerator : TransitionsGenerator<StackBufferState, ArcDistanceTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: StackBufferState): List<ArcDistanceTransition> {

    val transitions: MutableList<ArcDistanceTransition> = mutableListOf()

    transitions.add(Root(state, id = transitions.getNextId()))
    transitions.add(Shift(state, id = transitions.getNextId()))
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add multiple ArcLeft and ArcRight transitions.
   */
  private fun MutableList<ArcDistanceTransition>.addArcs(state: StackBufferState) {

    (0 until ArcDistance.maxTransitionDegree).reversed().forEach {
      (0 until minOf(state.stack.size, ArcDistance.maxTransitionDegree)).reversed().forEach {
        this.add(ArcLeft(state, degree = it, id = this.getNextId()))
        this.add(ArcRight(state, degree = it, id = this.getNextId()))
      }
    }
  }
}
