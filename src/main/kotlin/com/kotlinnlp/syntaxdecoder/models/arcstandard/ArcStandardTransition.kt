/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.arcstandard

import com.kotlinnlp.syntaxdecoder.state.templates.StackBufferState
import com.kotlinnlp.syntaxdecoder.state.State
import com.kotlinnlp.syntaxdecoder.Transition

/**
 * The State Transition of the ArcStandard transition system.
 *
 * @property state the [State] on which this transition operates
 */
abstract class ArcStandardTransition(
  state: StackBufferState
) : Transition<ArcStandardTransition, StackBufferState>(state)
