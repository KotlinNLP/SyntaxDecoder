/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state

/**
 * The atomic item that composes a [State].
 */
interface StateItem<out ErrorsType: ItemErrors, out RelevanceType: ItemRelevance> {

  /**
   * The unique id of this item.
   */
  val id: Int

  /**
   *
   */
  val errors: ErrorsType

  /**
   *
   */
  val relevance: RelevanceType
}
