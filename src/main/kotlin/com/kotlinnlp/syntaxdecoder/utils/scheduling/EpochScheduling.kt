/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.utils.scheduling

/**
 * Implement a listener that beats the occurrence of each epoch.
 */
interface EpochScheduling {

  /**
   * Beat the occurrence of a new epoch.
   */
  fun newEpoch()
}
