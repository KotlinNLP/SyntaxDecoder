/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import kotlin.reflect.KClass

/**
 * The Covington Transition System
 */
class Covington : TransitionSystem<CovingtonState, CovingtonTransition>() {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<CovingtonState> = CovingtonState::class

  /**
   * The TransitionsGenerator used to generate the next valid transitions given a [State].
   */
  override val transitionsGenerator: TransitionsGenerator<CovingtonState, CovingtonTransition>
    = CovingtonTransitionsGenerator()
}