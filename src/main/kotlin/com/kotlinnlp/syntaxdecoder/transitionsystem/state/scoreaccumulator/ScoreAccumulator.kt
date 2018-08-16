/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state.scoreaccumulator

import com.kotlinnlp.syntaxdecoder.utils.Clonable
import java.io.Serializable

/**
 * A score accumulator.
 */
abstract class ScoreAccumulator<SelfType: ScoreAccumulator<SelfType>> : Clonable<SelfType> {

  /**
   * The factory of a new [ScoreAccumulator].
   */
  interface Factory : Serializable {

    /**
     * @return a new score accumulator
     */
    operator fun invoke(): ScoreAccumulator<*>
  }

  /**
   * The accumulated score.
   */
  protected var accumulatedScore: Double = 0.0

  /**
   * The number of accumulations done.
   */
  protected var accumulations: Int = 0

  /**
   * @return the current score
   */
  abstract fun getCurrent(): Double

  /**
   * Accumulate the given [score].
   *
   * @param score a score
   */
  fun accumulate(score: Double) {
    this.accumulatedScore = this.getNextAccumulatedScore(score)
    this.accumulations++
  }

  /**
   * Simulate the accumulation of a given score and get the estimated future score.
   *
   * @param addingScore the adding score
   *
   * @return the estimated future score obtained accumulating the given [addingScore]
   */
  abstract fun estimateAccumulation(addingScore: Double): Double

  /**
   * @param addingScore the adding score
   *
   * @return the replacing value of the [accumulatedScore]
   */
  protected abstract fun getNextAccumulatedScore(addingScore: Double): Double
}
