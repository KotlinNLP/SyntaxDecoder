/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.easyfirst

import com.kotlinnlp.transitionsystems.*
import com.kotlinnlp.transitionsystems.state.templates.PendingListState
import kotlin.reflect.KClass

/**
 * The EasyFirst Transition System (Goldberg et al., 2010).
 *
 * In the easy-first parsing, arc-creation actions can happen between any two adjacent tokens.
 */
class EasyFirst : TransitionSystem<PendingListState, EasyFirstTransition>() {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  override val stateClass: KClass<PendingListState> = PendingListState::class

  /**
   * The [TransitionsGenerator] used to generate the next valid transitions given a [State].
   */
  override val transitionsGenerator = EasyFirstTransitionsGenerator()
}
