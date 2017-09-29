/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.arcstandard

import com.kotlinnlp.transitionsystems.*
import com.kotlinnlp.transitionsystems.arcstandard.transitions.*
import kotlin.reflect.KClass

/**
 * The ArcStandard Transition System (Nivre, 2008).
 *
 * The Arc-standard parsing is a form of shift-reduce parsing where arc-creations actions happen between
 * the top two elements on the stack.
 */
class ArcStandard : TransitionSystem<StackBufferState, ArcStandardTransition>() {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<StackBufferState> = StackBufferState::class

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun getValidTransitions(state: StackBufferState): List<ArcStandardTransition> {
    return listOf(
      Root(state),
      Shift(state),
      ArcLeft(state),
      ArcRight(state)
    ).filter { it.isAllowed }
  }
}
