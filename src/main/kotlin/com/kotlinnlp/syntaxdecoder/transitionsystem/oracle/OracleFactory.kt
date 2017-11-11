/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.oracle

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The OracleFactory.
 */
interface OracleFactory<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>{

  /**
   * Initialize a new Oracle with a [goldDependencyTree].
   *
   * @param goldDependencyTree a dependency tree
   *
   * @return a new Oracle
   */
  operator fun invoke(goldDependencyTree: DependencyTree): Oracle<StateType, TransitionType>
}