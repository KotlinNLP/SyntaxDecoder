/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.DependentsCounter
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState

/**
 * The ArcHybrid Non Projective Static Oracle.
 *
 * @property goldDependencyTree the dependency tree that the Oracle will try to reach
 */
class ArcHybridNPOracle(goldDependencyTree: DependencyTree)
  : Oracle<StackBufferState, ArcHybridTransition>(goldDependencyTree) {

  /**
   * The OracleFactory.
   */
  companion object Factory : OracleFactory<StackBufferState, ArcHybridTransition> {

    /**
     * Initialize a new Oracle with a [goldDependencyTree].
     *
     * @param goldDependencyTree a dependency tree
     *
     * @return a new Oracle
     */
    override fun invoke(goldDependencyTree: DependencyTree): Oracle<StackBufferState, ArcHybridTransition>
      = ArcHybridNPOracle(goldDependencyTree)
  }

  /**
   * The type of the Oracle.
   */
  override val type: Type = Type.STATIC

  /**
   * Dependent counter (support structure).
   */
  private var dependentsCounter = DependentsCounter(this.goldDependencyTree)

  /**
   * It contains the position of element i in the projective order
   */
  private var projectiveOrder: List<Int> = this.goldDependencyTree.projectiveOrder()

  /**
   * @return a copy of this Oracle
   */
  override fun copy(): Oracle<StackBufferState, ArcHybridTransition> {

    val clone = ArcHybridNPOracle(this.goldDependencyTree)

    clone.loss = this.loss
    clone.dependentsCounter = this.dependentsCounter.clone()
    clone.projectiveOrder = this.projectiveOrder

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
  override fun cost(transition: ArcHybridTransition): Int =
    when (transition) {
      is ArcLeft -> transition.calculateCost()
      is ArcRight -> transition.calculateCost()
      is Shift -> transition.calculateCost()
      is Root -> transition.calculateCost()
      is Swap -> transition.calculateCost()
      else  -> throw RuntimeException("Transition not in list.")
    }

  /**
   * Update the Oracle (the loss and its support structures) with a given [transition].
   * Make sure that the [transition] is allowed before calling the method.
   * Note that it is crucial to update the oracle before applying the [transition] to a state.
   *
   * @param transition a transition
   */
  override fun apply(transition: ArcHybridTransition) {
    if (transition is SyntacticDependency && transition.governorId != null){
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
    if (this@ArcHybridNPOracle.thereAreCorrectArcs(this.refState)
      || this@ArcHybridNPOracle.isSwapOptimal(this.refState)) 1 else 0

  /**
   * Calculate the cost of the Swap transition.
   *
   * @return the cost of this transition.
   */
  private fun Swap.calculateCost(): Int =
    if (this@ArcHybridNPOracle.isSwapOptimal(this.refState)) 0 else 1

  /**
   * @param state a state
   *
   * @return True if there are any zero-cost arc-transition in the given [state] configuration.
   */
  private fun thereAreCorrectArcs(state: StackBufferState): Boolean =
    ArcHybridTransitionsGenerator().generate(state)
      .filter { it is ArcLeft || it is ArcRight }
      .any { hasZeroCost(it) }

  /**
   * THe Swap is optimal only if PROJ(s0) > PROJ(b), where PROJ(i) is the projective order of the i-th element
   *
   * @param state a state
   *
   * @return a Boolean indicating whether the Swap is the optimal transition for the given [state]
   */
  private fun isSwapOptimal(state: StackBufferState): Boolean = Swap(state, id = -1).isAllowed
    && this.projectiveOrder[state.stack.last()] > this.projectiveOrder[state.buffer.first()]
}