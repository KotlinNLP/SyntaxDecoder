/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.utils

import com.kotlinnlp.syntaxdecoder.syntax.Deprel

/**
 * Groups the elements of the original deprels collection by the Deprel.Position property.
 *
 * The returned map preserves the order of the original collection.
 *
 * @return a new Map<Deprel.Position, List<Deprel>>
 */
fun List<Deprel>.groupByPosition(): Map<Deprel.Position, List<Deprel>> {
  val result = mutableMapOf<Deprel.Position, List<Deprel>>()

  Deprel.Position.values().forEach { position ->
    if (position != Deprel.Position.NULL && this.any { it.direction == position }) {
      result[position] = this.filter { it.direction == position }
    }
  }

  return result
}


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

/**
 * @param keys keys
 * @return true if all the map contain all the keys
 */
fun <K, V>Map<K, V>.containsKeys(vararg keys: K): Boolean = keys.all { this.containsKey(it) }
