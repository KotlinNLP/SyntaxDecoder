/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arceagerspine

import com.kotlinnlp.transitionsystems.*
import kotlin.reflect.KClass

/**
 * The ArcEagerSpine Transition System (Grella et al., 2016).
 */
class ArcEagerSpine : TransitionSystem<ArcEagerSpineState, ArcEagerSpineTransition>() {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<ArcEagerSpineState> = ArcEagerSpineState::class

  /**
   * The [TransitionsGenerator] used to generate the next valid transitions given a [State].
   */
  override val transitionsGenerator = ArcEagerSpineTransitionsGenerator()
}
