/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory

/**
 * The Covington Static Oracle.
 */
open class CovingtonOracle : Oracle<CovingtonState, CovingtonTransition>() {

  /**
   * The OracleFactory.
   */
  companion object Factory : OracleFactory<CovingtonState, CovingtonTransition> {

    /**
     * Initialize a new Oracle with a [goldDependencyTree].
     *
     * @param goldDependencyTree a dependency tree
     *
     * @return a new Oracle
     */
    override fun invoke(goldDependencyTree: DependencyTree): Oracle<CovingtonState, CovingtonTransition>
      = CovingtonOracle().initialize(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.STATIC

  /**
   * Initializes the support structures.
   */
  override fun initSupportStructure() = Unit

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<CovingtonState, CovingtonTransition> {

    val clone = CovingtonOracle()

    clone.loss = this.loss

    return clone
  }

  /**
   * Calculate the cost of the given [transition] in respect of the current state and the oracle configuration.
   * Make sure that the [transition] is allowed before calling the method.
   *
   * @param transition a state transition.
   *
   * @return the cost of the given [transition].
   */
  override fun calculateCostOf(transition: CovingtonTransition): Int =
    when (transition) {
      is ArcLeft -> transition.calculateCost()
      is ArcRight -> transition.calculateCost()
      is Shift -> transition.calculateCost()
      is NoArc -> transition.calculateCost()
      else  -> throw RuntimeException("Transition not in list.")
    }

  /**
   * Update the Oracle (the loss and its support structures) with a given [transition].
   * Make sure that the [transition] is allowed before calling the method.
   * Note that it is crucial to update the oracle before applying the [transition] to a state.
   *
   * @param transition a transition
   */
  override fun updateWith(transition: CovingtonTransition) = Unit

  /**
   * Calculate the cost of the ArcLeft transition.
   *
   * @return the cost of this transition.
   */
  private fun ArcLeft.calculateCost(): Int = if (this.isArcCorrect) 0 else 1

  /**
   * Calculate the cost of the ArcRight transition.
   *
   * @return the cost of this transition.
   */
  private fun ArcRight.calculateCost(): Int = if (this.isArcCorrect) 0 else 1

  /**
   * Calculate the cost of the NoArc transition.
   *
   * @return the cost of this transition.
   */
  open protected fun NoArc.calculateCost(): Int =
    if (this@CovingtonOracle.thereAreCorrectArcs(this.refState)) 1 else 0

  /**
   * Calculate the cost of the Shift transition.
   *
   * Shift is correct when no more attachments are pending in λ1 from or to j.
   *
   * @return the cost of this transition.
   */
  private fun Shift.calculateCost(): Int {

    val j: Int = this.refState.buffer.first()

    return when {
      this.refState.stack1.isEmpty() -> 0
      this.refState.stack1.none { goldDependencyTree.heads[it] == j || goldDependencyTree.heads[j] == it } -> 0
      else -> 1
    }
  }

  /**
   * @param state a state
   *
   * @return True if there are any zero-cost arc-transition in the given [state] configuration.
   */
  private fun thereAreCorrectArcs(state: CovingtonState): Boolean =
    CovingtonTransitionsGenerator().generate(state)
      .filter { it is ArcLeft || it is ArcRight }
      .any { hasZeroCost(it) }
}
