/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions.*

/**
 * The CovingtonTransitionsGenerator for the Covington Transition System.
 */
class CovingtonTransitionsGenerator : TransitionsGenerator<CovingtonState, CovingtonTransition>() {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: CovingtonState): List<CovingtonTransition>  = listOf(
    Shift(state, id = 0),
    NoArc(state, id = 1),
    ArcLeft(state, dependentStack1Index = 1, id = 2),
    ArcRight(state, governorStack1Index = 1, id = 3)
  )
}