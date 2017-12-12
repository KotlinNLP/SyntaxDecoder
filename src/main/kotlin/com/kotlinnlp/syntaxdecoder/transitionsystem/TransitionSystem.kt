/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import kotlin.reflect.KClass

/**
 * The TransitionSystem.
 */
abstract class TransitionSystem<StateType : State<StateType>, TransitionType : Transition<TransitionType, StateType>> {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  abstract protected val stateClass: KClass<StateType>

  /**
   * The [TransitionsGenerator] used to generate the next valid transitions given a [State].
   */
  abstract protected val transitionsGenerator: TransitionsGenerator<StateType, TransitionType>

  /**
   * Initialization function, mapping an ordered list of [itemIds] to an initial state.
   *
   * @property itemIds the list of item ids used to initialize the state
   * @property size the size of the sentence
   *
   * @return a new initialized [State]
   */
  fun getInitialState(itemIds: List<Int>, size: Int): StateType
    = this.stateClass.constructors.first().call(itemIds, size)

  /**
   * Generate valid transitions for a given [state].
   *
   * @param state a [State] of the system
   *
   * @return a list of valid [Transition]s for the given [state]
   */
  fun generateTransitions(state: StateType): List<TransitionType> = this.transitionsGenerator.generate(state)
}
