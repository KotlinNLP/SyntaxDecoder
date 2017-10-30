/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.context.items

import com.kotlinnlp.syntaxdecoder.utils.Clonable

/**
 * The relevance object associated to a [StateItem].
 */
interface ItemRelevance<SelfType: ItemRelevance<SelfType>> : Clonable<SelfType>
