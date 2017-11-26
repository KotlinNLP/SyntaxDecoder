/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.transitions.Shift
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState

/**
 * The ArcStandard Non-Deterministic Oracle.
 *
 * In the projective arc-standard non-deterministic oracle, the Shift transition
 * is retrieved if there are no left-arc and right-arc (as for the static oracle)
 * or if there exists a right dependent of s0 into the buffer.
 *
 * @property goldDependencyTree the dependency tree that the Oracle will try to reach
 */
open class ArcStandardNonDetOracle(goldDependencyTree: DependencyTree)
  : ArcStandardOracle(goldDependencyTree) {

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
      = ArcStandardNonDetOracle(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.NON_DETERMINISTIC

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<StackBufferState, ArcStandardTransition> {

    val clone = ArcStandardNonDetOracle(this.goldDependencyTree)

    clone.loss = this.loss
    clone.dependentsCounter = this.dependentsCounter.clone()

    return clone
  }

  /**
   * Calculate the cost of the Shift transition.
   */
  override fun Shift.calculateCost(): Int =
    when {
      !this@ArcStandardNonDetOracle.thereAreCorrectArcs(this.refState) -> 0
      this.refState.buffer.any { goldDependencyTree.heads[it] == this.refState.stack.last() } -> 0
      else -> 1
    }
}
