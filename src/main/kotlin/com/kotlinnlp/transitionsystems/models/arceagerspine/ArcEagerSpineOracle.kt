/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arceagerspine

import com.kotlinnlp.transitionsystems.*
import com.kotlinnlp.transitionsystems.models.arceagerspine.transitions.ArcLeft
import com.kotlinnlp.transitionsystems.models.arceagerspine.transitions.ArcRight
import com.kotlinnlp.transitionsystems.models.arceagerspine.transitions.Root
import com.kotlinnlp.transitionsystems.models.arceagerspine.transitions.Shift
import com.kotlinnlp.transitionsystems.syntax.SyntacticDependency
import com.kotlinnlp.transitionsystems.utils.subListFrom

/**
 * The ArcEagerSpine Dynamic Oracle.
 */
class ArcEagerSpineOracle : Oracle<ArcEagerSpineState, ArcEagerSpineTransition>() {

  /**
   * The type of the Oracle.
   */
  override val type: Type = Oracle.Type.DYNAMIC

  /**
   * The set of reachable dependents.
   */
  private lateinit var reachableDependents: MutableSet<Int>

  /**
   * Initialize the support structures.
   */
  override fun initSupportStructure() {
    this.reachableDependents = goldDependencyTree.ids.toMutableSet()
  }

  /**
   * Calculate the cost of the given [transition] in respect of the current state and the oracle configuration.
   * Make sure that the [transition] is allowed before calling the method.
   *
   * @param transition a state transition.
   *
   * @return the cost of the given [transition].
   */
  override fun calculateCostOf(transition: ArcEagerSpineTransition): Int =
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
  override fun updateWith(transition: ArcEagerSpineTransition) {
    this.loss += this.calculateCostOf(transition)

    when (transition) {
      is ArcLeft -> transition.removeUnreachableDependents()
      is ArcRight -> transition.removeUnreachableDependents()
      is Shift -> transition.removeUnreachableDependents()
      is Root -> transition.removeUnreachableDependents()
      else  -> throw RuntimeException("Transition not in list.")
    }
  }

  /**
   *  Calculate the cost of the ArcLeft transition.
   */
  private fun ArcLeft.calculateCost(): Int {

    var cost = 0

    if (this.isDependentReachable && !this.isArcCorrect) cost += 1

    cost += this.state.stack.last().sumBy {
      reachableDependents.intersect(goldDependencyTree.dependents[it].right).size
    }

    return cost
  }

  /**
   * Calculate the cost of the ArcRight transition.
   */
  private fun ArcRight.calculateCost(): Int {

    var cost = 0

    if (this.isDependentReachable && !this.isArcCorrect) cost += 1

    cost += reachableDependents.intersect(
      goldDependencyTree.dependents[this.dependentId].left).size

    cost += this.state.stack.last().subListFrom(this.governorSpineIndex + 1)?.sumBy {
      reachableDependents.intersect(goldDependencyTree.dependents[it].right).size
    } ?: 0

    return cost
  }

  /**
   * Calculate the cost of the Shift transition.
   */
  private fun Shift.calculateCost(): Int {

    var cost = 0

    val b0: Int = this.state.buffer.first()

    if (reachableDependents.contains(b0)
      && (goldDependencyTree.heads[b0] != null
       && goldDependencyTree.heads[b0]!! < b0)) cost += 1

    cost += reachableDependents.intersect(goldDependencyTree.dependents[b0].left).size

    return cost
  }

  /**
   * Calculate the cost of the Root transition.
   */
  private fun Root.calculateCost(): Int = if (reachableDependents.size > 1) 1 else 0

  /**
   * Removes from [reachableDependents] the dependents that would no longer be reachable by applying this transition.
   */
  private fun ArcLeft.removeUnreachableDependents() {
    reachableDependents.remove(this.dependentId)

    this.state.stack.last().forEach {
      reachableDependents.removeAll(goldDependencyTree.dependents[it].right)
    }
  }

  /**
   * Removes from [reachableDependents] the dependents that would no longer be reachable by applying this transition.
   */
  private fun ArcRight.removeUnreachableDependents() {
    reachableDependents.remove(this.dependentId)
    reachableDependents.removeAll(goldDependencyTree.dependents[this.dependentId].left)

    this.state.stack.last().subListFrom(this.governorSpineIndex + 1)?.forEach {
      reachableDependents.removeAll(goldDependencyTree.dependents[it].right)
    }
  }

  /**
   * Removes from [reachableDependents] the dependents that would no longer be reachable by applying this transition.
   */
  private fun Shift.removeUnreachableDependents() {
    val b0: Int = this.state.buffer.first()

    if (goldDependencyTree.heads[b0] != null && goldDependencyTree.heads[b0]!! < b0) {
      reachableDependents.remove(b0)
    }

    reachableDependents.removeAll(goldDependencyTree.dependents[b0].left)
  }

  /**
   * Removes from [reachableDependents] the dependents that would no longer be reachable by applying this transition.
   */
  private fun Root.removeUnreachableDependents() {
    reachableDependents.remove(this.dependentId)
  }

  /**
   * True if the dependent of the Syntactic Dependency is still reachable.
   */
  private val SyntacticDependency.isDependentReachable get() =
    reachableDependents.contains(this.dependentId)
}
