/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcspine

import com.kotlinnlp.transitionsystems.*
import com.kotlinnlp.transitionsystems.arcspine.transitions.*
import com.kotlinnlp.transitionsystems.utils.DependentsCounter
import com.kotlinnlp.transitionsystems.utils.secondToLast

/**
 * The ArcSpine Static Oracle.
 */
open class ArcSpineOracle : Oracle<ArcSpineState, ArcSpineTransition>() {

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.STATIC

  /**
   * Dependent counter (support structure).
   */
  private lateinit var dependentsCounter: DependentsCounter

  /**
   * Initializes the support structures.
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
  override fun calculateCostOf(transition: ArcSpineTransition): Int =
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
  override fun updateWith(transition: ArcSpineTransition) {
    if (transition is SyntacticDependency && transition.governorId != null){
      this.dependentsCounter.decrease(transition.governorId!!)
    }
  }

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
  open protected fun Shift.calculateCost(): Int =
    if (this.state.stack.size <= 1) 0 else {

      val s0h = goldDependencyTree.heads[this.state.stack.last().root]
      val s1h = goldDependencyTree.heads[this.state.stack.secondToLast().root]

      val s0ls = this.state.stack.last().leftSpine
      val s1rs = this.state.stack.secondToLast().rightSpine

      if (s0ls.contains(s1h) || s1rs.contains(s0h)) 1 else 0
    }
}
