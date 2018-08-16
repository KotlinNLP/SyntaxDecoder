/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.oracle

import com.kotlinnlp.dependencytree.DependencyTree

/**
 * The DependentsCounter.
 *
 * @property counter
 */
class DependentsCounter(private val counter: MutableMap<Int, Int>) {

  companion object {

    /**
     * @param dependencyTree a dependency tree
     *
     * @return a new DependentsCounter
     */
    operator fun invoke(dependencyTree: DependencyTree): DependentsCounter {

      val counter = mutableMapOf<Int, Int>()

      dependencyTree.elements.forEach {
        counter[it] = dependencyTree.getDependents(it).size
      }

      return DependentsCounter(counter)
    }
  }

  /**
   * @param elementId an element id.
   *
   * @return the number of dependents of the given [elementId]
   */
  operator fun get(elementId: Int): Int = this.counter.getValue(elementId)

  /**
   * Decrease by one the dependents of the the given [elementId].
   *
   * @param elementId an element id
   */
  fun decrease(elementId: Int) {
    require(this.counter.getValue(elementId) > 0)
    this.counter[elementId] = this.counter.getValue(elementId) - 1
  }

  /**
   * @param elementId an element id
   *
   * @return True if the element at [elementId] is resolved (no more dependents).
   */
  fun isResolved(elementId: Int): Boolean = this[elementId] == 0

  /**
   * @return a new copy of this [DependentsCounter]
   */
  fun clone(): DependentsCounter = DependentsCounter(this.counter.toMutableMap())
}
