/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.Shift

/**
 * The ArcSpine Non Deterministic Oracle
 *
 * @property goldDependencyTree the dependency tree that the Oracle will try to reach
 */
class ArcSpineNonDetOracle(goldDependencyTree: DependencyTree)
  : ArcSpineOracle(goldDependencyTree) {

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
    override fun invoke(goldDependencyTree: DependencyTree): Oracle<ArcSpineState, ArcSpineTransition> =
      ArcSpineNonDetOracle(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.NON_DETERMINISTIC

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<ArcSpineState, ArcSpineTransition> {

    val clone = ArcSpineNonDetOracle(this.goldDependencyTree)

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

    if (this.refState.stack.size > 1) {

      val s0: ArcSpineState.StackElement = this.refState.stack.last()

      when {
        s0.root < goldDependencyTree.getHead(s0.root) ?: -1 -> 0
        this.refState.buffer.any { s0.rightSpine.contains(goldDependencyTree.getHead(it)) } -> 0
        else -> 1
      }

    } else {
      0
    }
}
