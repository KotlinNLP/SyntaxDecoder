/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcdistance

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState
import kotlin.reflect.KClass

/**
 * The ArcDistance Transition System.
 *
 * This [TransitionSystem] allows arcs between the roots of non-adjacent subtrees (non-adjacent arc transitions)
 * maintaining linear time complexity but with incomplete coverage of non-projective structures.
 *
 * The ArcDistance is based on the model of Attardi (2006). Notice that the implemented transitions rules are slightly
 * different from those reported in the [original paper](http://acl.ldc.upenn.edu/W/W06/W06-2922.pdf).
 * The fundamental difference is that each reduction rule puts back into the buffer the token that has been
 * connected as head. This allows for such token to be further involved in new transitions.
 */
class ArcDistance : TransitionSystem<StackBufferState, ArcDistanceTransition>() {

  companion object {

    /**
     * The maximum distance in the stack of the nodes involved in the newly constructed dependency.
     * When this distance (degree) is 0 the transition system is able to handle only projective dependencies.

     * Note: the reported coverage in ArcDistance (2006) is already very high when the system is restricted
     * to transitions of degree two or three (here values 1, 2).
     */
    val maxTransitionDegree: Int = 3
  }

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<StackBufferState> = StackBufferState::class

  /**
   * The TransitionsGenerator used to generate the next valid transitions given a [State].
   */
  override val transitionsGenerator = ArcDistanceTransitionsGenerator()
}
