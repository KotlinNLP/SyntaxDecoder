/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition

/**
 * The State Transition of the Convington transition system.
 *
 * @property refState the [State] on which this transition operates
 */
abstract class CovingtonTransition(
  refState: CovingtonState
) : Transition<CovingtonTransition, CovingtonState>(refState)