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
class NLCovingtonTransitionsGenerator : TransitionsGenerator<CovingtonState, CovingtonTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: CovingtonState): List<CovingtonTransition> {

    val transitions: MutableList<CovingtonTransition> = mutableListOf()

    transitions.add(Shift(state, id = transitions.getNextId()))
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add multiple ArcLeft and ArcRight transitions.
   */
  private fun MutableList<CovingtonTransition>.addArcs(state: CovingtonState) {

    state.stack1.indices.forEach { k ->

      if (state.dependencyTree.getHead(state.buffer.first()) == null) {
        this.add(ArcRight(state, governorStack1Index = k + 1, id = this.getNextId()))
      }

      if (state.dependencyTree.getHead(state.stack1[state.stack1.lastIndex - k]) == null){
        this.add(ArcLeft(state, dependentStack1Index = k + 1, id = this.getNextId()))
      }
    }
  }
}
