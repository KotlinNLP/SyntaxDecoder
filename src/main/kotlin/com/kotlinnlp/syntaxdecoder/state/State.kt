/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.state

import com.kotlinnlp.syntaxdecoder.syntax.DependencyTree

/**
 * The State.
 *
 * @property itemIds the list of item ids used to initialize the state
 */
abstract class State<SelfType: State<SelfType>>(val itemIds: List<Int>) {

  /**
   * Contains the set of arcs already created.
   */
  var dependencyTree: DependencyTree = DependencyTree(this.itemIds.size)
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