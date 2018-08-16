/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.PendingListState
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.DependentsCounter

/**
 * The EasyFirst Static Oracle.
 *
 * To efficiently identify complete dependents we hold a counter [dependentsCounter] for each element
 * which is initialized to the number of dependents the element has in the gold tree.
 * When applying an attachment the counter of the dependent’s gold head element is decreased.
 * When the counter reaches 0, the sub-tree rooted at that word has no pending dependents,
 * and is considered complete (resolved).
 *
 * @property goldDependencyTree the dependency tree that the Oracle will try to reach
 */
class EasyFirstOracle(goldDependencyTree: DependencyTree)
  : Oracle<PendingListState, EasyFirstTransition>(goldDependencyTree) {

  /**
   * The OracleFactory.
   */
  companion object Factory : OracleFactory<PendingListState, EasyFirstTransition> {

    /**
     * Initialize a new Oracle with a [goldDependencyTree].
     *
     * @param goldDependencyTree a dependency tree
     *
     * @return a new Oracle
     */
    override fun invoke(goldDependencyTree: DependencyTree): Oracle<PendingListState, EasyFirstTransition>
      = EasyFirstOracle(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.STATIC

  /**
   * Dependent counter (support structure).
   */
  private var dependentsCounter = DependentsCounter(this.goldDependencyTree)

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<PendingListState, EasyFirstTransition> {

    val clone = EasyFirstOracle(this.goldDependencyTree)

    clone.loss = this.loss
    clone.dependentsCounter = this.dependentsCounter.clone()

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
  override fun cost(transition: EasyFirstTransition): Int =
    when (transition) {
      is ArcLeft -> transition.calculateCost()
      is ArcRight -> transition.calculateCost()
      is Root -> transition.calculateCost()
      else  -> throw RuntimeException("Transition not in list.")
    }

  /**
   * Update the Oracle (the loss and its support structure) with a given [transition].
   * Make sure that the [transition] is allowed before calling the method.
   * Note that it is crucial to update the oracle before applying the [transition] to a state.
   *
   * @param transition a transition
   */
  override fun apply(transition: EasyFirstTransition) {
    if (transition.governorId != null) {
      this.dependentsCounter.decrease(transition.governorId!!)
    }
  }

  /**
   * Calculate the cost of the ArcLeft transition.
   *
   * @return the cost of this transition.
   */
  private fun ArcLeft.calculateCost(): Int
    = if (this.isArcCorrect && dependentsCounter.isResolved(this.dependentId)) 0 else 1

  /**
   * Calculate the cost of the ArcRight transition.
   *
   * @return the cost of this transition.
   */
  private fun ArcRight.calculateCost(): Int
    = if (this.isArcCorrect && dependentsCounter.isResolved(this.dependentId)) 0 else 1

  /**
   * Calculate the cost of the Root transition.
   *
   * @return the cost of this transition.
   */
  private fun Root.calculateCost(): Int = if (goldDependencyTree.getHead(this.dependentId) == null) 0 else 1
}
