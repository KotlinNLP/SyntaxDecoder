/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers

import com.kotlinnlp.transitionsystems.Transition

/**
 * Sort the actions by their 'score' and then by their 'priority'.
 */
fun List<Transition<*, *>.Action>.sortByScoreAndPriority() =
  this.sortedWith(compareByDescending<Transition<*, *>.Action> { it.score }.thenBy { it.transition.priority })

/**
 * Set all the errors at zero.
 */
fun List<Transition<*, *>.Action>.resetErrors() =
  this.forEach { it.error = 0.0 }

/**
 * Set all the scores at zero.
 */
fun List<Transition<*, *>.Action>.resetScores() =
  this.forEach { it.score = 0.0 }
