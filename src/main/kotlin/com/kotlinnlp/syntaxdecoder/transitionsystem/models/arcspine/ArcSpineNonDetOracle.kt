/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine

import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.Shift
import com.kotlinnlp.syntaxdecoder.syntax.DependencyTree

/**
 * The ArcSpine Non Deterministic Oracle
 */
class ArcSpineNonDetOracle : ArcSpineOracle() {

  /**
   * The OracleFactory.
   */
  companion object Factory : OracleFactory<ArcSpineState, ArcSpineTransition> {

    /**
     * Initialize a new Oracle with a [goldDependencyTree].
     *
     * @param goldDependencyTree a dependency tree
     *
     * @return a new Oracle
     */
    override fun invoke(goldDependencyTree: DependencyTree): Oracle<ArcSpineState, ArcSpineTransition>
      = ArcSpineNonDetOracle().initialize(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.NON_DETERMINISTIC

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<ArcSpineState, ArcSpineTransition> {

    val clone = ArcSpineNonDetOracle()

    clone.loss = this.loss
    clone.dependentsCounter = this.dependentsCounter.clone()

    return clone
  }

  /**
   * Calculate the cost of the Shift transition.
   *
   * @return the cost of this transition.
   */
  override fun Shift.calculateCost(): Int =
    if (this.state.stack.size <= 1) 0 else {
      val s0: ArcSpineState.StackElement = this.state.stack.last()

      when {
        s0.root < goldDependencyTree.heads[s0.root] ?: -1 -> 0
        this.state.buffer.any { s0.rightSpine.contains(goldDependencyTree.heads[it]) } -> 0
        else -> 1
      }
    }
}
