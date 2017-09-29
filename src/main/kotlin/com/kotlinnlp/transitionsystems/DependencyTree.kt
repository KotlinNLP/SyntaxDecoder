/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

import com.kotlinnlp.transitionsystems.utils.secondToLast
import kotlin.coroutines.experimental.buildSequence

/**
 * This class contains convenient methods to build and navigate a tree.
 *
 * A tree is represented by a simple Array of Integer, where the indexes are the node-id and the values the heads.
 * The root nodes have heads = null.
 */
class DependencyTree {

  companion object {
    /**
     *
     */
    operator fun invoke(size: Int): DependencyTree {
      val tree = DependencyTree()
      tree.initElementsUntil(size - 1)
      return tree
    }
  }

  /**
   *
   */
  class Dependents {
    val left = ArrayList<Int>()
    val right = ArrayList<Int>()
    val count: Int get() = this.left.size + this.right.size

    val all = setOf(*this.left.toArray(), *this.right.toArray())
  }

  /**
   *
   */
  var heads = ArrayList<Int?>()
    private set

  /**
   *
   */
  val ids: List<Int> get() = this.heads.indices.toList()

  /**
   *
   */
  var deprels = ArrayList<Deprel?>()
    private set

  /**
   *
   */
  var dependents = ArrayList<Dependents>()
    private set

  /**
   *
   */
  fun clear() {
    this.heads.clear()
    this.deprels.clear()
    this.dependents.clear()
  }

  /**
   *
   */
  fun setArc(dependentId: Int, governorId: Int?, deprel: Deprel? = null) {

    this.initElementsUntil(elementId = maxOf(dependentId, governorId ?: 0))

    this.heads[dependentId] = governorId
    this.deprels[dependentId] = deprel

    if (governorId != null) this.setDependent(dependentId, governorId)
  }

  /**
   *
   */
  fun match(matchTree: DependencyTree): Boolean = this.matchStructure(matchTree) && this.matchDeprels(matchTree)

  /**
   *
   */
  fun matchStructure(matchTree: DependencyTree, skipNullHeads: Boolean = false): Boolean =
    if (skipNullHeads){
      this.heads.zip(matchTree.heads).all { it.first == null || it.first == it.second }
    } else {
      this.heads == matchTree.heads
    }

  /**
   *
   */
  fun matchDeprels(matchTree: DependencyTree): Boolean = this.deprels == matchTree.deprels

  /**
   *
   */
  fun isUnattached(elementId: Int): Boolean = this.heads[elementId] == null

  /**
   *
   */
  fun isAttached(elementId: Int): Boolean = this.heads[elementId] != null

  /**
   *
   */
  fun initElementsUntil(elementId: Int){
    require(this.heads.size == this.dependents.size)

    if (this.heads.lastIndex < elementId) {
      (this.heads.lastIndex + 1 .. elementId).forEach { this.addElement() }
    }
  }

  /**
   *
   */
  private fun addElement() {
    this.heads.add(null)
    this.deprels.add(null)
    this.dependents.add(Dependents())
  }

  /**
   *
   */
  private fun setDependent(dependentId: Int, governorId: Int) {

    val governorDependents = this.dependents[governorId]

    if (dependentId < governorId) {
      governorDependents.left.add(dependentId)
    } else {
      governorDependents.right.add(dependentId)
    }
  }

  override fun toString(): String {

    val result = ArrayList<String>()

    this.heads.indices.forEach { index ->
      result.add(
        index.toString()
          + "\t" + (this.heads[index] ?: -1).toString()
          + "\t" + this.deprels[index].toString())
    }

    return result.joinToString("\n")
  }


  /**
   * @param id the id of a node of the tree
   *
   * @returns all words going from the id-node up the path to the root
   */
  fun getAncestors(id: Int) = buildSequence<Int> {

    val array = DependencyTree@heads

    var head: Int? = id
    var i = 0

    while (array[head!!] != head && i < array.size) {
      head = array[head]

      if (head == null) {
        break
      } else {
        yield(head)
      }
      i += 1
    }
  }

  /**
   * @return True if the tree contains cycles.
   *
   * Implementation note: in an acyclic tree, the path from each word following the head relation
   * upwards always ends at the root node.
   */
  fun containsCycle(): Boolean {

    this.heads.indices.forEach { id ->

      val seen = mutableListOf<Int?>(id)

      this.getAncestors(id).forEach { ancestor ->
        if (ancestor in seen) {
          return true
        } else {
          seen.add(ancestor)
        }
      }
    }

    return false
  }

  /**
   * @return True if the array represents a directed acyclic graph.
   */
  fun isTree(): Boolean {

    if (!this.checkHeadsBoundaries()) return false

    val h = arrayOfNulls<Int>(this.heads.size)

    this.heads.indices.forEach { i ->

      var k = i

      loop@ while (k > 0) when {

        h[k] == i -> return false // not a tree

        h[k] in 0..(i - 1) -> break@loop

        this.heads[k] == null -> break@loop

        else -> {
          h[k] = i
          k = this.heads[k]!!
        }
      }
    }

    return !this.containsCycle()
  }

  /**
   * @param id the id of a node of the tree.
   * @return True if the id-node is involved in a non-projective arc.
   *
   * Implementation note: an arc h -> d, h < d is non-projective if there is a token k, h < k < d
   * such that h is not an ancestor of k. Same for h -> d, h > d.
   */
  fun isNonProjectiveArc(id: Int): Boolean {

    val head = this.heads[id]

    return if (head == id || head == null) {

      false

    } else {

      val range = if (head < id)
        IntRange(head + 1, id - 1)
      else
        IntRange(id + 1, head - 1)

      range.any { k -> this.getAncestors(k).none { it == head } }
    }
  }

  /**
   * @return True is the tree contains at least one non-projective arc.
   */
  fun isNonProjectiveTree(): Boolean = this.heads.indices.any { this.isNonProjectiveArc(it) }

  /**
   * @return the number of roots.
   */
  fun Array<Int?>.countRoots(): Int = this.count { it == null }

  /**
   * @return True if the tree is single root.
   */
  fun Array<Int?>.isSingleRoot(): Boolean = this.countRoots() == 1

  /**
   *
   */
  fun clone(): DependencyTree {

    val clonedTree = DependencyTree()

    clonedTree.heads = ArrayList(this.heads)
    clonedTree.deprels = ArrayList(this.deprels)
    clonedTree.dependents= ArrayList(this.dependents)

    return clonedTree
  }

  /**
   *
   */
  private fun leftDependents(index: Int) = this.dependents[index].left

  /**
   *
   */
  private fun rightDependents(index: Int) = this.dependents[index].left

  /**
   *
   */
  fun rc1(index: Int?): Int? {
    if (index == null) return null

    val dependents = this.rightDependents(index)

    return if (dependents.isEmpty()) {
      null
    } else {
      dependents.last()
    }
  }

  /**
   *
   */
  fun lc1(index: Int?): Int? {
    if (index == null) return null

    val dependents = this.leftDependents(index)

    return if (dependents.isEmpty()) {
      null
    } else {
      dependents.last()
    }
  }

  /**
   *
   */
  fun rc2(index: Int?): Int? {
    if (index == null) return null

    val dependents = this.rightDependents(index)

    return if (dependents.size < 2) {
      null
    } else {
      dependents.secondToLast()
    }
  }

  /**
   *
   */
  fun lc2(index: Int?): Int? {
    if (index == null) return null

    val dependents = this.leftDependents(index)

    return if (dependents.size < 2) {
      null
    } else {
      dependents.secondToLast()
    }
  }

  /**
   * @return True if all the nodes on the tree are not-null and in the range [0, last-id]
   */
  private fun checkHeadsBoundaries(): Boolean = this.heads.indices.none {
    this.heads[it] != null && (this.heads[it]!! < 0 || this.heads[it]!! > this.heads.lastIndex)
  }
}
