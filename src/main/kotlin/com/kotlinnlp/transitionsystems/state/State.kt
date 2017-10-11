/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state

import com.kotlinnlp.transitionsystems.DependencyTree

/**
 * The State.
 *
 * @property tokens the list of tokens used to initialize the state.
 */
abstract class State<SelfType: State<SelfType>>(protected val tokens: List<Int>) {

  /**
   * Contains the set of arcs already created.
   */
  var dependencyTree: DependencyTree = DependencyTree(this.tokens.size)
    protected set

  /**
   * True when the state reach the end.
   */
  abstract val isTerminal: Boolean

  /**
   * @return a new copy of this [State].
   */
  abstract fun clone(): SelfType
}
