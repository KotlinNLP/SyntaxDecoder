/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.oracle

import com.kotlinnlp.syntaxdecoder.syntax.DependencyTree

/**
 * The DependentsCounter.
 *
 * @property countList
 */
class DependentsCounter(private val countList: ArrayList<Int>) {

  companion object {

    /**
     * @param dependencyTree a dependency tree
     *
     * @return a new DependentsCounter
     */
    operator fun invoke(dependencyTree: DependencyTree): DependentsCounter {

      val countList = ArrayList<Int>()

      dependencyTree.heads.indices.forEach {
        countList.add(dependencyTree.dependents[it].count)
      }

      return DependentsCounter(countList)
    }
  }

  /**
   * @param elementId an element id.
   *
   * @return the number of dependents of the [index]th-element.
   */
  operator fun get(elementId: Int): Int = this.countList[elementId]

  /**
   * Decrease by one the dependents of the the given [elementId].
   *
   * @param elementId an element id
   */
  fun decrease(elementId: Int) {
    require(this.countList[elementId] > 0)
    this.countList[elementId]--
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
  fun clone(): DependentsCounter = DependentsCounter(ArrayList(this.countList))
}
