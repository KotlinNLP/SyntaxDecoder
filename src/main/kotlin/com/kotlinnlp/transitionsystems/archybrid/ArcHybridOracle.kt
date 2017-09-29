/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.archybrid

import com.kotlinnlp.transitionsystems.*
import com.kotlinnlp.transitionsystems.archybrid.transitions.*
import com.kotlinnlp.transitionsystems.utils.DependentsCounter

/**
 * The ArcHybrid Static Oracle.
 *
 * To efficiently identify complete dependents we hold a counter [dependentsCounter] for each element
 * which is initialized to the number of dependents the element has in the gold tree.
 * When applying an attachment the counter of the dependent’s gold head element is decreased.
 * When the counter reaches 0, the sub-tree rooted at that word has no pending dependents,
 * and is considered complete (resolved).
 */
class ArcHybridOracle : Oracle<StackBufferState, ArcHybridTransition>() {

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.STATIC

  /**
   * Dependent counter (support structure).
   */
  private lateinit var dependentsCounter: DependentsCounter

  /**
   * Initialize the support structures.
   */
  override fun initSupportStructure() {
    this.dependentsCounter = DependentsCounter(this.goldDependencyTree)
  }

  /**
   * Calculate the cost of the given [transition] in respect of the current state and the oracle configuration.
   * Make sure that the [transition] is allowed before calling the method.
   *
   * @param transition a state transition.
   *
   * @return the cost of the given [transition].
   */
  override fun calculateCostOf(transition: ArcHybridTransition): Int =
    when (transition) {
      is ArcLeft -> transition.calculateCost()
      is ArcRight -> transition.calculateCost()
      is Shift -> transition.calculateCost()
      is Root -> transition.calculateCost()
      else  -> throw RuntimeException("Transition not in list.")
    }

  /**
   * Update the Oracle (the loss and its support structures) with a given [transition].
   * Make sure that the [transition] is allowed before calling the method.
   * Note that it is crucial to update the oracle before applying the [transition] to a state.
   *
   * @param transition a transition
   */
  override fun updateWith(transition: ArcHybridTransition) {
    if (transition is DependencyRelation && transition.governorId != null){
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
  private fun Root.calculateCost(): Int = if (goldDependencyTree.heads[this.dependentId] == null) 0 else 1

  /**
   * Calculate the cost of the Shift transition.
   *
   * @return the cost of this transition.
   */
  private fun Shift.calculateCost(): Int =
    if (ArcHybrid().getValidTransitions(this.state)
      .filter { it is ArcLeft || it is ArcRight }
      .none { hasZeroCost(this) }) 0 else 1
}
