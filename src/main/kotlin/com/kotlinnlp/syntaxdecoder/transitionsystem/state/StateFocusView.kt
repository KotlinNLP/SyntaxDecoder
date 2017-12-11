/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.Focusable

/**
 * The view of a transition system state related to a given [focus] point.
 *
 * @property state the transition system state
 * @property focus the index of the focus item
 */
open class StateFocusView<StateType>(state: StateType, val focus: Int) : StateView<StateType>(state)
  where StateType : State<StateType>, StateType : Focusable
