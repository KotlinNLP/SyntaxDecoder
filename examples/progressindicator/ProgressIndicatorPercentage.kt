/* Copyright 2016-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import java.io.OutputStream

/**
 *
 */
class ProgressIndicatorPercentage(total: Int, outputStream: OutputStream = System.out) :
  ProgressIndicator(total = total, outputStream = outputStream) {

  /**
   *
   */
  override fun getProgressString(): String = "[${this.perc}%]"

}
