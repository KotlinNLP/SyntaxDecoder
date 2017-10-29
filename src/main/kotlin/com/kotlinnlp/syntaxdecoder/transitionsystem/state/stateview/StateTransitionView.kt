/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State

/**
 * The [StateView] based on [state] and [transitions].
 *
 * @property state a [State]
 * @property transitions a list of [Transition]
 */
data class StateTransitionView<StateType : State<StateType>, TransitionType : Transition<TransitionType, StateType>>(
  val state: StateType,
  val transitions: List<TransitionType>
) : StateView<StateType>
