/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions.ArcLeft
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions.ArcRight
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions.Shift

/**
 * The CovingtonTransitionsGenerator for the Covington Transition System.
 */
class NLCovingtonTransitionsGenerator : TransitionsGenerator<CovingtonState, CovingtonTransition> {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: CovingtonState): List<CovingtonTransition> {

    val transitions = ArrayList<CovingtonTransition>()

    transitions.addShift(state)
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add Shift transitions (if allowed)
   */
  private fun ArrayList<CovingtonTransition>.addShift(state: CovingtonState) {
    val shift = Shift(state)
    if (shift.isAllowed) this.add(shift)
  }

  /**
   * Add multiple ArcLeft and ArcRight transitions.
   */
  private fun ArrayList<CovingtonTransition>.addArcs(state: CovingtonState){

    if (state.buffer.isNotEmpty()) {

      state.stack1.indices.forEach { k ->

        if (state.dependencyTree.heads[state.buffer.first()] == null) {
          this.add(ArcRight(state, governorStack1Index = k + 1))
        }

        if (state.dependencyTree.heads[state.stack1[state.stack1.lastIndex - k]] == null){
          this.add(ArcLeft(state, dependentStack1Index = k + 1))
        }
      }
    }
  }
}