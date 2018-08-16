/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state

import com.kotlinnlp.dependencytree.DependencyTree
import java.util.concurrent.atomic.AtomicInteger

/**
 * The State.
 *
 * @property itemIds the list of item ids used to initialize the state
 * @property size the size of the sentence used to initialize the state
 */
abstract class State<SelfType: State<SelfType>>(
  val itemIds: List<Int>,
  val size: Int
) {

  /**
   * Used to keep track of a state configuration at a certain time.
   */
  var track = AtomicInteger(0)

  /**
   * Contains the set of arcs already created.
   */
  var dependencyTree: DependencyTree = DependencyTree(this.itemIds)
    protected set

  /**
   * True when the state reach the end.
   */
  abstract val isTerminal: Boolean

  /**
   * @return a new copy of this [State]
   */
  abstract fun copy(): SelfType
}
