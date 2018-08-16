/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine

import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator

/**
 * The TransitionsGenerator for the ArcEagerSpine Transition System.
 */
class ArcEagerSpineTransitionsGenerator : TransitionsGenerator<ArcEagerSpineState, ArcEagerSpineTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: ArcEagerSpineState): List<ArcEagerSpineTransition> {

    val transitions: MutableList<ArcEagerSpineTransition> = mutableListOf()

    transitions.add(Root(state, id = transitions.getNextId()))
    transitions.add(Shift(state, id = transitions.getNextId()))
    transitions.add(ArcLeft(state, id = transitions.getNextId()))

    transitions.addArcsRight(state)

    return transitions
  }

  /**
   * Add multiple ArcRight transitions
   */
  private fun MutableList<ArcEagerSpineTransition>.addArcsRight(state: ArcEagerSpineState) {

    if (state.stack.isNotEmpty()) {

      state.stack.last().indices.forEach { k ->
        this.add(ArcRight(state, governorSpineIndex = k, id = this.getNextId()))
      }
    }
  }
}
