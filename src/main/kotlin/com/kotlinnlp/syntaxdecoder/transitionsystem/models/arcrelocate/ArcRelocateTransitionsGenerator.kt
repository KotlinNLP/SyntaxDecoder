/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcrelocate

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcrelocate.transitions.*

/**
 * The TransitionsGenerator for the ArcRelocate Transition System.
 */
class ArcRelocateTransitionsGenerator : TransitionsGenerator<StackBufferState, ArcRelocateTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: StackBufferState): List<ArcRelocateTransition> = listOf(
    Root(state, id = 0),
    Shift(state, id = 1),
    ArcLeft(state, id = 2),
    ArcRight(state, id = 3),
    Wait(state, id = 4)
  )
}
