/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.models.easyfirst

/**
 * Define an Arc involving a focus element in the pending list.
 */
interface FocusedArc {

  /**
   * The index of the focus element in the pending list.
   */
  val pendingListFocus: Int
}
