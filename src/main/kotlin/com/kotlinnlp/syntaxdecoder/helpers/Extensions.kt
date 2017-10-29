/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.helpers

import com.kotlinnlp.syntaxdecoder.Transition
import com.kotlinnlp.syntaxdecoder.state.State

/**
 * A [Comparator] of actions by score and then by transition priority.
 */
private val scoreTransitionComparator
  = compareByDescending<Transition<*, *>.Action> { it.score }.thenBy { it.transition.priority }

/**
 * Sort a list of actions by score and then by transition priority.
 */
fun <TransitionType: Transition<TransitionType, StateType>, StateType: State<StateType>>
  List<Transition<TransitionType, StateType>.Action>.sortByScoreAndPriority()
  = this.sortedWith(scoreTransitionComparator)
