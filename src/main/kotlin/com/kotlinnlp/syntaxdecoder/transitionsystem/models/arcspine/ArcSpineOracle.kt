/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.*
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.DependentsCounter
import com.kotlinnlp.syntaxdecoder.utils.secondToLast

/**
 * The ArcSpine Static Oracle.
 *
 * @property goldDependencyTree the dependency tree that the Oracle will try to reach
 */
open class ArcSpineOracle(goldDependencyTree: DependencyTree)
  : Oracle<ArcSpineState, ArcSpineTransition>(goldDependencyTree) {

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
      = ArcSpineOracle(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.STATIC

  /**
   * Dependent counter (support structure).
   */
  protected var dependentsCounter = DependentsCounter(this.goldDependencyTree)

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<ArcSpineState, ArcSpineTransition> {

    val clone = ArcSpineOracle(this.goldDependencyTree)

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
  override fun cost(transition: ArcSpineTransition): Int =
    when (transition) {
      is ArcLeft -> transition.calculateCost()
      is ArcRight -> transition.calculateCost()
      is Shift -> transition.calculateCost()
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
  override fun apply(transition: ArcSpineTransition) {
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
  private fun Root.calculateCost(): Int = if (goldDependencyTree.getHead(this.dependentId) == null) 0 else 1

  /**
   * Calculate the cost of the Shift transition.
   *
   * @return the cost of this transition.
   */
  protected open fun Shift.calculateCost(): Int =

    if (this.refState.stack.size > 1) {

      val s0h = goldDependencyTree.getHead(this.refState.stack.last().root)
      val s1h = goldDependencyTree.getHead(this.refState.stack.secondToLast().root)

      val s0ls = this.refState.stack.last().leftSpine
      val s1rs = this.refState.stack.secondToLast().rightSpine

      if (s0ls.contains(s1h) || s1rs.contains(s0h)) 1 else 0

    } else {
      0
    }
}
