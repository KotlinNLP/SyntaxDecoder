/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions.ArcLeft
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions.ArcRight
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions.Root
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.transitions.Shift

/**
 * The TransitionsGenerator for the ArcSwift Transition System.
 */
class ArcSwiftTransitionsGenerator : TransitionsGenerator<StackBufferState, ArcSwiftTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: StackBufferState): List<ArcSwiftTransition> {

    val transitions = ArrayList<ArcSwiftTransition>()

    transitions.addRoot(state)
    transitions.addShift(state)
    transitions.addArcs(state)

    return transitions
  }

  /**
   * Add Root transitions (if allowed)
   */
  private fun ArrayList<ArcSwiftTransition>.addRoot(state: StackBufferState) {
    val root = Root(state, id = this.getNextId())
    if (root.isAllowed) this.add(root)
  }

  /**
   * Add Shift transitions (if allowed)
   */
  private fun ArrayList<ArcSwiftTransition>.addShift(state: StackBufferState) {
    val shift = Shift(state, id = this.getNextId())
    if (shift.isAllowed) this.add(shift)
  }

  /**
   * Add Arcs transitions (if allowed)
   */
  private fun ArrayList<ArcSwiftTransition>.addArcs(state: StackBufferState) {
    this.addArcLeft(state)
    this.addArcRight(state)
  }

  /**
   * Add ArcLeft transitions (if allowed)
   */
  private fun ArrayList<ArcSwiftTransition>.addArcLeft(state: StackBufferState) {

    if (state.buffer.isNotEmpty()){
      val si: Int = state.stack.indexOfFirst { state.dependencyTree.isNotAssigned(it) }
      if (si > -1) this.add(ArcLeft(state, dependentStackIndex =  si, id = this.getNextId()))
    }
  }

  /**
   * Add multiple transitions of type RightArc (if allowed)
   */
  private fun ArrayList<ArcSwiftTransition>.addArcRight(state: StackBufferState) {

    if (state.buffer.size > 1 || (state.buffer.size == 1 && state.unattachedStackElements.size == 1)){
      loop@for (si in 0 until state.stack.size){
        this.add(ArcRight(state, governorStackIndex = si, id = this.getNextId()))
        if (state.dependencyTree.isNotAssigned(state.stack[si])) break@loop
      }
    }
  }

  /**
   * @return the list of unattached elements on the stack.
   */
  private val StackBufferState.unattachedStackElements: List<Int> get() =
    this.stack.filter { this.dependencyTree.isNotAssigned(it) }
}
