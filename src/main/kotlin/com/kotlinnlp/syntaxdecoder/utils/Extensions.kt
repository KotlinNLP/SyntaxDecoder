/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.utils

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * A [Comparator] of actions by score and then by transition priority.
 */
private val scoreTransitionComparator
  = compareByDescending<Transition<*, *>.Action> { it.score }.thenBy { it.transition.priority }

/**
 * Sort a list of actions by descending score and then by transition priority.
 */
fun <TransitionType: Transition<TransitionType, StateType>, StateType: State<StateType>>
  List<Transition<TransitionType, StateType>.Action>.sortByScoreAndPriority()
  = this.sortedWith(scoreTransitionComparator)

/**
 * @return a map of transitions to the actions generated by each of them
 */
fun <TransitionType: Transition<TransitionType, StateType>, StateType: State<StateType>>
  List<Transition<TransitionType, StateType>.Action>.groupByTransitions():
  Map<TransitionType, List<Transition<TransitionType, StateType>.Action>>
  = this.groupBy { it.transition }

/**
 * @return the list of unique transitions that generated this actions
 */
fun <TransitionType: Transition<TransitionType, StateType>, StateType: State<StateType>>
  List<Transition<TransitionType, StateType>.Action>.toTransitions(): List<TransitionType>
  = this.groupByTransitions().keys.toList()

/**
 * @return a map of transition ids to the related transitions that generated this actions
 */
fun <TransitionType: Transition<TransitionType, StateType>, StateType: State<StateType>>
  List<Transition<TransitionType, StateType>.Action>.toTransitionsMap(): Map<Int, TransitionType>
  = this.toTransitions().associateBy { it.id }

/**
 * Remove the last element
 */
fun <E>ArrayList<E>.removeLast() {
  this.removeAt(this.lastIndex)
}

/**
 *
 */
fun <E>ArrayList<E>.removeFrom(index: Int): ArrayList<E> {
  this.subList(index, this.size).clear()
  return this
}

/**
 * Returns a list containing elements at indices in the specified [indices] range removing them from the origin.
 */
fun <E>ArrayList<E>.extractAndRemove(indices: IntRange): List<E> {
  val elements = this.slice(indices)
  indices.reversed().forEach { this.removeAt(it) }
  return elements
}

/**
 *
 */
fun <E>ArrayList<E>.subListFrom(fromIndex: Int): MutableList<E>? =
  if (fromIndex > this.lastIndex){
    null
  } else {
    this.subList(fromIndex, this.lastIndex)
  }

/**
 * @return the last element removing it
 */
fun <E>ArrayList<E>.pop(): E {
  val element = this.last()
  this.removeAt(this.lastIndex)
  return element
}

/**
 * removes the first element from an items and returns that element.
 * This method changes the length of the items.
 */
fun <E>ArrayList<E>.removeFirst(): E {
  val element = this.first()
  this.removeAt(0)
  return element
}

/**
 * @return the second to last element
 */
fun <E>ArrayList<E>.secondToLast(): E {
  return this[this.lastIndex - 1]
}

/**
 * @param  index gets the nth element
 *         -index gets the nth-to-last element
 * @return the element at the given index or null if the index is not in the array range
 */
fun <E>ArrayList<E>.getItemOrNull(index: Int): E? {
  val accessIndex = if (index < 0) this.size + index else index
  return if (accessIndex in 0..this.lastIndex) this[accessIndex] else null
}
