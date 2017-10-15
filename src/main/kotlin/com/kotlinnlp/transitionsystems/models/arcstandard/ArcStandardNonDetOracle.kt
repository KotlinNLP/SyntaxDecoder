/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arcstandard

import com.kotlinnlp.transitionsystems.Oracle
import com.kotlinnlp.transitionsystems.OracleFactory
import com.kotlinnlp.transitionsystems.models.arcstandard.transitions.Shift
import com.kotlinnlp.transitionsystems.state.templates.StackBufferState
import com.kotlinnlp.transitionsystems.syntax.DependencyTree

/**
 * The ArcStandard Non-Deterministic Oracle.
 *
 * In the projective arc-standard non-deterministic oracle, the Shift transition
 * is retrieved if there are no left-arc and right-arc (as for the static oracle)
 * or if there exists a right dependent of s0 into the buffer.
 */
open class ArcStandardNonDetOracle : ArcStandardOracle() {

  /**
   * The OracleFactory.
   */
  companion object Factory : OracleFactory<StackBufferState, ArcStandardTransition> {

    /**
     * Initialize a new Oracle with a [goldDependencyTree].
     *
     * @param goldDependencyTree a dependency tree
     *
     * @return a new Oracle
     */
    override fun invoke(goldDependencyTree: DependencyTree): Oracle<StackBufferState, ArcStandardTransition>
      = ArcStandardNonDetOracle().initialize(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.NON_DETERMINISTIC

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<StackBufferState, ArcStandardTransition> {

    val clone = ArcStandardNonDetOracle()

    clone.loss = this.loss
    clone.dependentsCounter = this.dependentsCounter.clone()

    return clone
  }

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
