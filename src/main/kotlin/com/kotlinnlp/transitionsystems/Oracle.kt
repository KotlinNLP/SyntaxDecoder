/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

import com.kotlinnlp.transitionsystems.state.State

/**
 * The Oracle.
 *
 * The Oracle is used to derive optimal transition sequences from gold dependency trees.
 */
abstract class Oracle<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>> {

  /**
   * The Oracle Type.
   *
   * @property STATIC an oracle is deterministic if it returns a single canonical transition.
   *
   * @property NON_DETERMINISTIC an oracle is non-deterministic if it returns the set of all transitions
   *           that are optimal with respect to the gold tree.
   *
   * @property DYNAMIC a oracles that is both non-deterministic and is well-defined and correct
   *           for every configuration that is reachable from a given state.
   */
  enum class Type {
    STATIC,
    NON_DETERMINISTIC,
    DYNAMIC
  }

  /**
   * The type of the Oracle.
   */
  abstract val type: Type

  /**
   * The loss of the Oracle in a given moment.
   */
  var loss: Int = 0

  /**
   * The gold dependency tree that the Oracle will try to reach.
   */
  protected lateinit var goldDependencyTree: DependencyTree

  /**
   * Initialize the support structures.
   */
  abstract protected fun initSupportStructure()

  /**
   * Calculate the cost of the given [transition] in respect of the current state and the oracle configuration.
   * Make sure that the [transition] is allowed before calling the method.
   *
   * @param transition a state transition.
   *
   * @return the cost of the given [transition].
   */
  abstract fun calculateCostOf(transition: TransitionType): Int

  /**
   * Update the Oracle (the loss and its support structures) with a given [transition].
   * Make sure that the [transition] is allowed before calling the method.
   * Note that it is crucial to update the oracle before applying the [transition] to a state.
   *
   * @param transition a transition
   */
  abstract fun updateWith(transition: TransitionType)

  /**
   * Initialize the Oracle with a gold [dependencyTree].
   * It is possible to initialize the same oracle multiple times.
   *
   * @param dependencyTree a gold dependency tree.
   *
   * @return this oracle.
   */
  fun initialize(dependencyTree: DependencyTree): Oracle<StateType, TransitionType> {

    this.goldDependencyTree = dependencyTree
    this.loss = 0
    this.initSupportStructure()

    return this
  }

  /**
   * True if the transition has zero cost.
   */
  fun hasZeroCost(transition: TransitionType): Boolean = this.calculateCostOf(transition) == 0

  /**
   *  True if the action has zero cost.
   */
  fun hasZeroCost(action: Transition<TransitionType, StateType>.Action): Boolean = this.hasZeroCost(action.transition)

  /**
   * True if the transition is correct.
   */
  fun isCorrect(transition: TransitionType): Boolean
    = this.hasZeroCost(transition) && (transition !is SyntacticDependency || transition.isArcCorrect)

  /**
   * True if the action is correct.
   */
  fun isCorrect(action: Transition<TransitionType, StateType>.Action): Boolean
    = this.isCorrect(action.transition) && (action !is DependencyRelation || action.isDeprelCorrect())

  /**
   * @param dependentId a dependentId.
   * @param governorId a governorId.
   *
   * @return True if the arc defined by the given [dependentId] and [governorId] is correct.
   */
  fun isArcCorrect(dependentId: Int, governorId: Int?): Boolean
    = this.goldDependencyTree.heads[dependentId] == governorId

  /**
   * @return True if the arc is correct.
   */
  val SyntacticDependency.isArcCorrect: Boolean get() =
    isArcCorrect(dependentId = this.dependentId, governorId = this.governorId)

  /**
   * @return True if the dependency relation (deprel) is correct.
   */
  private fun DependencyRelation.isDeprelCorrect(): Boolean
    = this.deprel == null || this.deprel == this@Oracle.getGoldDeprel(this.dependentId)

  /**
   * @param dependent a dependent id.
   *
   * @return the deprel of this dependent on the gold dependency tree (can be null).
   */
  private fun getGoldDeprel(dependent: Int): Deprel? = this.goldDependencyTree.deprels[dependent]
}
