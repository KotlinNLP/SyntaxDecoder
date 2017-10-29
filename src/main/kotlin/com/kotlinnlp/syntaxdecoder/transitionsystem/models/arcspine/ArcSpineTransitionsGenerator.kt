/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine

import com.kotlinnlp.syntaxdecoder.helpers.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.ArcLeft
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.ArcRight
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.Root
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.Shift
import com.kotlinnlp.syntaxdecoder.utils.secondToLast

/**
 * The TransitionsGenerator for the ArcHybrid Transition System.
 */
class ArcSpineTransitionsGenerator : TransitionsGenerator<ArcSpineState, ArcSpineTransition> {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: ArcSpineState): List<ArcSpineTransition> {

    val transitions = ArrayList<ArcSpineTransition>()

    transitions.addRoot(state)
    transitions.addShift(state)
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add Root transitions (if allowed)
   */
  private fun ArrayList<ArcSpineTransition>.addRoot(state: ArcSpineState){
    val root = Root(state)
    if (root.isAllowed) this.add(root)
  }

  /**
   * Add Shift transitions (if allowed)
   */
  private fun ArrayList<ArcSpineTransition>.addShift(state: ArcSpineState){
    val shift = Shift(state)
    if (shift.isAllowed) this.add(shift)
  }

  /**
   * Add multiple ArcLeft and ArcRight transitions.
   */
  private fun ArrayList<ArcSpineTransition>.addArcs(state: ArcSpineState){

    if (state.stack.size > 1) {
      state.stack.last().leftSpine.indices.forEach { k -> this.add(ArcLeft(state, k)) }
      state.stack.secondToLast().rightSpine.indices.forEach { k -> this.add(ArcRight(state, k)) }
    }
  }
}
