/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.state.scoreaccumulator

/**
 * The score accumulator that accumulate by logarithmic summation.
 */
class LogarithmicAccumulator : ScoreAccumulator<LogarithmicAccumulator>() {

  /**
   * The factory of a new [LogarithmicAccumulator].
   */
  companion object Factory : ScoreAccumulator.Factory {

    /**
     * Private val used to serialize the class (needed from Serializable)
     */
    @Suppress("unused")
    private const val serialVersionUID: Long = 1L

    /**
     * @return a new score accumulator
     */
    override fun invoke() = LogarithmicAccumulator()
  }

  /**
   * The lower bound to limit accumulating scores (to avoid overflow and manage zero scores).
   */
  private val eps = 1.0e-08

  /**
   * Get the current score converting the domain (-inf, 0.0] to (0.0, 1.0].
   *
   * @return the current score
   */
  override fun getCurrent(): Double = this.convertDomain(this.accumulatedScore)

  /**
   * Simulate the accumulation of a given score and get the estimated future score.
   * The resulting score is the joint probability of the current one with the given [addingScore] (after have
   * transformed it by natural logarithm, to avoid underflow).
   *
   * @param addingScore the adding score (must be in the range [0.0, 1.0]
   *
   * @return the estimated future score obtained accumulating the given [addingScore]
   */
  override fun estimateAccumulation(addingScore: Double): Double
    = this.convertDomain(this.getNextAccumulatedScore(addingScore))

  /**
   * @param addingScore the adding score (must be in the range [0.0, 1.0]
   *
   * @return the replacing value of the [accumulatedScore]
   */
  override fun getNextAccumulatedScore(addingScore: Double): Double {
    require(addingScore in 0.0 .. 1.0) { "Invalid score: $addingScore, must be in range [0.0, 1.0]." }

    return this.accumulatedScore + Math.log(maxOf(addingScore, this.eps))
  }

  /**
   * @return a copy of this accumulator
   */
  override fun copy(): LogarithmicAccumulator {

    val clonedAccumulator = LogarithmicAccumulator()

    clonedAccumulator.accumulatedScore = this.accumulatedScore
    clonedAccumulator.accumulations = this.accumulations

    return clonedAccumulator
  }

  /**
   * Converting the domain of the score from (-inf, 0.0] to (0.0, 1.0].
   *
   * @return the score in the new domain
   */
  private fun convertDomain(score: Double): Double = 1 / (-0.1 * score + 1)
}
