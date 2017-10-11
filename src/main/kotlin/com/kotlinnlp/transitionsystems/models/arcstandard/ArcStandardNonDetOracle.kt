/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arcstandard

import com.kotlinnlp.transitionsystems.Oracle
import com.kotlinnlp.transitionsystems.models.arcstandard.transitions.Shift

/**
 * The ArcStandard Non-Deterministic Oracle.
 *
 * In the projective arc-standard non-deterministic oracle, the Shift transition
 * is retrieved if there are no left-arc and right-arc (as for the static oracle)
 * or if there exists a right dependent of s0 into the buffer.
 */
open class ArcStandardNonDetOracle : ArcStandardOracle() {

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.NON_DETERMINISTIC

  /**
   * Calculate the cost of the Shift transition.
   */
  override fun Shift.calculateCost(): Int =
    when {
      !this@ArcStandardNonDetOracle.thereAreCorrectArcs(this.state) -> 0
      this.state.buffer.any { goldDependencyTree.heads[it] == this.state.stack.last() } -> 0
      else -> 1
    }
}
