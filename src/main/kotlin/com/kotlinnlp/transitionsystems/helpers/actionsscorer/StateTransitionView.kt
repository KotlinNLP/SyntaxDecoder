/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.Transition

/**
 * The [StateView] based on [state] and [transition].
 *
 * @property state a [State]
 * @property transition a [Transition]
 */
data class StateTransitionView<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>>
(
  val state: StateType,
  val transition: Transition<TransitionType, StateType>
) : StateView
