/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The TransitionsGenerator.
 */
abstract class TransitionsGenerator<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>> {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  abstract fun generate(state: StateType): List<TransitionType>

  /**
   * @return the next id
   */
  protected fun MutableList<TransitionType>.getNextId(): Int = this.lastIndex + 1
}
