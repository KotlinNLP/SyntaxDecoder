/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state

import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.utils.Clonable

/**
 * The [DecodingContext] extends the input with adding properties.
 */
interface DecodingContext<SelfType: DecodingContext<SelfType, ItemType>, ItemType: StateItem<ItemType, *, *>>
  : Clonable<SelfType> {

  /**
   * A list of [StateItem].
   */
  val items: List<ItemType>

  /**
   * A callback called after the learn of a sentence.
   */
  fun afterLearn()
}
