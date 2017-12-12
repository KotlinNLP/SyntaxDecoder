/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.context

import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.utils.Clonable

/**
 * The [InputContext] extends the input with adding properties.
 */
interface InputContext<SelfType: InputContext<SelfType, ItemType>, ItemType: StateItem<ItemType, *, *>>
  : Clonable<SelfType> {

  /**
   * A list of [StateItem].
   */
  val items: List<ItemType>

  /**
   * The length of the sentence.
   */
  val length: Int

  /**
   * @return the items id to initialize a [State]
   */
  fun getInitialStateItemsId() = this.items.map { it.id }
}
