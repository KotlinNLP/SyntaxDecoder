/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state.items

import com.kotlinnlp.transitionsystems.utils.Clonable

/**
 * The atomic item that composes a [State].
 */
interface StateItem<
  SelfType: StateItem<SelfType, ErrorsType, RelevanceType>,
  ErrorsType: ItemErrors<ErrorsType>,
  RelevanceType: ItemRelevance<RelevanceType>>
  :
  Clonable<SelfType> {

  /**
   * The unique id of this item.
   */
  val id: Int

  /**
   * The errors associated to this item.
   */
  var errors: ErrorsType?

  /**
   * The relevance associated to this item.
   */
  var relevance: RelevanceType?
}
