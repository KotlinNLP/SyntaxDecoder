/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcdistance

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.templates.StackBufferState


/**
 * The State Transition of the ArcDistance transition system.
 *
 * @property refState the [State] on which this transition operates
 * @property id the transition id
 */
abstract class ArcDistanceTransition(
  refState: StackBufferState,
  id: Int
) : Transition<ArcDistanceTransition, StackBufferState>(refState, id)
