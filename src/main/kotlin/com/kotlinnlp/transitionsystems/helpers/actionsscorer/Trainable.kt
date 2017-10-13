/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.BatchScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.EpochScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.ExampleScheduling

/**
 * Define a trainable object, with time scheduling and errors propagation.
 */
interface Trainable : ExampleScheduling, BatchScheduling, EpochScheduling {

  /**
   * Backward errors through this object, starting from the output, eventually accumulating them into proper structures.
   * Errors are required to be already set into the output.
   * If [propagateToInput] is true errors are propagated to the input.
   *
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   */
  fun backward(propagateToInput: Boolean)

  /**
   * Update the trainable components of this object.
   */
  fun update()
}
