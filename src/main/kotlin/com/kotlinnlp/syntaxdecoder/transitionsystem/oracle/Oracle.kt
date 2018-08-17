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
   * Update the Oracle (the loss and its support structure) with a given [transition].
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
   * @param transition a transition
   *
   * @return whether the given transition has zero cost
   */
  fun hasZeroCost(transition: TransitionType): Boolean = this.cost(transition) == 0

  /**
   * @param action an action
   *
   * @return whether the given action has zero cost
   */
  fun hasZeroCost(action: Transition<TransitionType, StateType>.Action): Boolean = this.hasZeroCost(action.transition)

  /**
   * @param action an action
   *
   * @return whether the given action is correct.
   */
  fun isCorrect(action: Transition<TransitionType, StateType>.Action): Boolean =
    action.transition.isAllowed && this.isCorrect(action.transition)
      && (action !is DependencyRelation || action.isCorrect())

  /**
   * @param dependentId the dependent id
   * @param governorId the governor id
   *
   * @return whether the arc defined by the given [dependentId] and [governorId] is correct
   */
  fun isArcCorrect(dependentId: Int, governorId: Int?): Boolean =
    this.goldDependencyTree.getHead(dependentId) == governorId

  /**
   * @return whether the arc of this dependency is correct
   */
  val SyntacticDependency.isArcCorrect: Boolean get() =
    isArcCorrect(dependentId = this.dependentId!!, governorId = this.governorId)

  /**
   * @param item an item of the [goldDependencyTree]
   *
   * @return whether the head of the given item is on its left
   */
  protected fun headOnLeft(item: Int): Boolean {

    val head: Int? = goldDependencyTree.getHead(item)

    return head != null && goldDependencyTree.getPosition(head) < goldDependencyTree.getPosition(item)
  }

  /**
   * @param item an item of the [goldDependencyTree]
   *
   * @return whether the head of the given item is on its right
   */
  protected fun headOnRight(item: Int): Boolean {

    val head: Int? = goldDependencyTree.getHead(item)

    return head != null && goldDependencyTree.getPosition(head) > goldDependencyTree.getPosition(item)
  }

  /**
   * @param transition a transition
   *
   * @return whether the given transition is correct
   */
  private fun isCorrect(transition: TransitionType): Boolean =
    this.hasZeroCost(transition) && (transition !is SyntacticDependency || transition.isArcCorrect)

  /**
   * @return whether the dependency relation (deprel and posTag) is correct
   */
  private fun DependencyRelation.isCorrect(): Boolean = this.isDeprelCorrect() && this.isPosTagCorrect()

  /**
   * @return whether the syntactic component of a dependency relation (deprel) is correct
   */
  private fun DependencyRelation.isDeprelCorrect(): Boolean =
    this.deprel == null || this.deprel == this@Oracle.getGoldDeprel(this.dependentId!!)

  /**
   * @return whether the morphological component of a dependency relation (posTag) is correct
   */
  private fun DependencyRelation.isPosTagCorrect(): Boolean =
    this.posTag == null || this.posTag == this@Oracle.getGoldPosTag(this.dependentId!!)

  /**
   * @param dependentId the id of a dependent
   *
   * @return the deprel of this dependent on the gold dependency tree (can be null)
   */
  private fun getGoldDeprel(dependentId: Int): Deprel? = this.goldDependencyTree.getDeprel(dependentId)

  /**
   * @param dependentId the id of a dependent
   *
   * @return the pos tag of this dependent on the gold dependency tree (can be null)
   */
  private fun getGoldPosTag(dependentId: Int): POSTag? = this.goldDependencyTree.getPosTag(dependentId)
}
