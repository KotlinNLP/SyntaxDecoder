/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.archybrid

import com.kotlinnlp.syntaxdecoder.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.helpers.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.models.archybrid.transitions.ArcLeft
import com.kotlinnlp.syntaxdecoder.models.archybrid.transitions.ArcRight
import com.kotlinnlp.syntaxdecoder.models.archybrid.transitions.Root
import com.kotlinnlp.syntaxdecoder.models.archybrid.transitions.Shift

/**
 * The TransitionsGenerator for the ArcHybrid Transition System.
 */
class ArcHybridTransitionsGenerator : TransitionsGenerator<StackBufferState, ArcHybridTransition> {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: StackBufferState): List<ArcHybridTransition> = listOf(
    Root(state),
    Shift(state),
    ArcLeft(state),
    ArcRight(state)
  ).filter { it.isAllowed }
}
