/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.attardi

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import kotlin.reflect.KClass

/**
 * The Attardi Transition System (Attardi, 2006).
 *
 * Attardi (2006) instead introduced transitions that add dependency arcs between the roots of non-adjacent subtrees
 * (nonadjacent arc transitions) –  maintaining linear time complexity but with incomplete coverage of
 * non-projective structures.
 *
 * Notice that the implemented transitions rules are slightly different from those reported in Attardi (2006).
 * The fundamental difference is that each reduction rule puts back into the input queue the token that has been
 * connected as head. This allows for such token to be further involved in new transitions.
 */
class Attardi : TransitionSystem<StackBufferState, AttardiTransition>() {

  companion object {

    /**
     *
     */
    val maxNonProjectiveDistance: Int = 1
  }

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<StackBufferState> = StackBufferState::class

  /**
   * The TransitionsGenerator used to generate the next valid transitions given a [State].
   */
  override val transitionsGenerator = AttardiTransitionsGenerator()
}
