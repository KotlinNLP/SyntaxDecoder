/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arceagerspine

import com.kotlinnlp.transitionsystems.*
import com.kotlinnlp.transitionsystems.arceagerspine.transitions.*
import kotlin.reflect.KClass

/**
 * The ArcEagerSpine Transition System (Grella et al., 2016).
 */
class ArcEagerSpine : TransitionSystem<ArcEagerSpineState, ArcEagerSpineTransition>() {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<ArcEagerSpineState> = ArcEagerSpineState::class

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun getValidTransitions(state: ArcEagerSpineState): List<ArcEagerSpineTransition> {

    val transitions = ArrayList<ArcEagerSpineTransition>()

    transitions.addRoot(state)
    transitions.addShift(state)
    transitions.addArcLeft(state)
    transitions.addArcsRight(state)

    return transitions
  }

  /**
   * Add Root transition (if allowed).
   */
  private fun ArrayList<ArcEagerSpineTransition>.addRoot(state: ArcEagerSpineState){
    val root = Root(state)
    if (root.isAllowed) this.add(root)
  }

  /**
   * Add Shift transition (if allowed).
   */
  private fun ArrayList<ArcEagerSpineTransition>.addShift(state: ArcEagerSpineState){
    val shift = Shift(state)
    if (shift.isAllowed) this.add(shift)
  }

  /**
   * Add ArcLeft transition (if allowed).
   */
  private fun ArrayList<ArcEagerSpineTransition>.addArcLeft(state: ArcEagerSpineState){
    val arcLeft = ArcLeft(state)
    if (arcLeft.isAllowed) this.add(arcLeft)
  }

  /**
   * Add multiple ArcRight transitions (if allowed)
   */
  private fun ArrayList<ArcEagerSpineTransition>.addArcsRight(state: ArcEagerSpineState) {
    if (state.stack.isNotEmpty() && state.buffer.isNotEmpty()) {
      state.stack.last().indices.forEach { k -> this.add(ArcRight(state, k)) }
    }
  }
}