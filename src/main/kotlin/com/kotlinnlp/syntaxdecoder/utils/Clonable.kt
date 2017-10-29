/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.utils

/**
 * Defines an object that has the method copy(), which returns a new object of the same type, containing a copy of its
 * content.
 */
interface Clonable<out SelfType> {

  /**
   * @return a copy of this object
   */
  fun copy(): SelfType
}
