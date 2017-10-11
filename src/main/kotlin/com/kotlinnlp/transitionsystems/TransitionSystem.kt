/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

import com.kotlinnlp.transitionsystems.helpers.TransitionsGenerator
import com.kotlinnlp.transitionsystems.state.State
import kotlin.reflect.KClass

/**
 * The TransitionSystem.
 */
abstract class TransitionSystem<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>> {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  abstract protected val stateClass: KClass<StateType>

  /**
   * The [TransitionsGenerator] used to generate the next valid transitions given a [State].
   */
  abstract protected val transitionsGenerator: TransitionsGenerator<StateType, TransitionType>

  /**
   * Initialization function, mapping an ordered list of [tokens] to an initial state.
   *
   * @param tokens the list of tokens used to initialize the state.
   *
   * @return a new initial [State].
   */
  fun getInitialState(tokens: List<Int>): StateType = this.stateClass.constructors.first().call(tokens)

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  fun getValidTransitions(state: StateType): List<TransitionType> = this.transitionsGenerator.generate(state)
}
