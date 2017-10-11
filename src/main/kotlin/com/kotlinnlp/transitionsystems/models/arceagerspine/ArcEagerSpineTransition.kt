/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.arceagerspine

import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.Transition

/**
 * The State Transition of the ArcEager transition system.
 *
 * @property state the [State] on which this transition operates
 */
abstract class ArcEagerSpineTransition(
  state: ArcEagerSpineState
) : Transition<ArcEagerSpineTransition, ArcEagerSpineState>(state)
