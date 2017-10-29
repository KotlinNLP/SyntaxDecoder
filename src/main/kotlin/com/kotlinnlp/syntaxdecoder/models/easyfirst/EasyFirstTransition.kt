/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.models.easyfirst

import com.kotlinnlp.syntaxdecoder.state.templates.PendingListState
import com.kotlinnlp.syntaxdecoder.state.State
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.Transition

/**
 * The State Transition of the EasyFirst transition system.
 *
 * @property state the [State] on which this transition operates
 */
abstract class EasyFirstTransition(
  state: PendingListState
) : Transition<EasyFirstTransition, PendingListState>(state), SyntacticDependency
