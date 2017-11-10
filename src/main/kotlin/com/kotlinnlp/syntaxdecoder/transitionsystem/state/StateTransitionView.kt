/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition

/**
 * The view of a transition system state related to a given [transition].
 *
 * @property state a transition system state
 * @property transition a transition of the [state]
 */
open class StateTransitionView<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>(
  state: StateType,
  val transition: TransitionType
) : StateView<StateType>(state)
