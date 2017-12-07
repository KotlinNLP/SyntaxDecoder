/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.oracle

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.dependencytree.Deprel
import com.kotlinnlp.dependencytree.POSTag
import com.kotlinnlp.syntaxdecoder.syntax.DependencyRelation
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The Oracle.
 *
 * The Oracle is used to derive optimal transition sequences from gold dependency trees.
 *
 * @property goldDependencyTree the dependency tree that the Oracle will try to reach
 */
abstract class Oracle<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>(
  val goldDependencyTree: DependencyTree
) {

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
   * Calculate the cost of the given [transition] in respect of the current state and the oracle configuration.
   * Make sure that the [transition] is allowed before calling the method.
   *
   * @param transition a state transition.
   *
   * @return the cost of the given [transition].
   */
  abstract fun cost(transition: TransitionType): Int

  /**
   * Update the Oracle (the loss and its support structures) with a given [transition].
   * Make sure that the [transition] is allowed before calling the method.
   * Note that it is crucial to update the oracle before applying the [transition] to a state.
   *
   * @param transition a transition
   */
  abstract fun apply(transition: TransitionType)

  /**
   * @return a copy of this Oracle
   */
  abstract fun copy(): Oracle<StateType, TransitionType>

  /**
   * True if the transition has zero cost.
   */
  fun hasZeroCost(transition: TransitionType): Boolean = this.cost(transition) == 0

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
    = action.transition.isAllowed && this.isCorrect(action.transition)
    && (action !is DependencyRelation || action.isCorrect())

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
    isArcCorrect(dependentId = this.dependentId!!, governorId = this.governorId)

  /**
   * @return True if the dependency relation (deprel and posTag) is correct.
   */
  private fun DependencyRelation.isCorrect(): Boolean = this.isDeprelCorrect() && this.isPosTagCorrect()

  /**
   * @return True if the syntactic component of a dependency relation (deprel) is correct.
   */
  private fun DependencyRelation.isDeprelCorrect(): Boolean
    = this.deprel == null || this.deprel == this@Oracle.getGoldDeprel(this.dependentId!!)

  /**
   * @return True if the morphological component of a dependency relation (posTag) is correct.
   */
  private fun DependencyRelation.isPosTagCorrect(): Boolean
    = this.posTag == null || this.posTag == this@Oracle.getGoldPosTag(this.dependentId!!)

  /**
   * @param dependent a dependent id.
   *
   * @return the deprel of this dependent on the gold dependency tree (can be null).
   */
  private fun getGoldDeprel(dependent: Int): Deprel? = this.goldDependencyTree.deprels[dependent]

  /**
   * @param dependent a dependent id.
   *
   * @return the pos tag of this dependent on the gold dependency tree (can be null).
   */
  private fun getGoldPosTag(dependent: Int): POSTag? = this.goldDependencyTree.posTags[dependent]
}
