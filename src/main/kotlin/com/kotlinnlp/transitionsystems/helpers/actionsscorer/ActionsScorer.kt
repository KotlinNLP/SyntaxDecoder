/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.Transition

/**
 * The ActionsScorer.
 *
 * @property featuresExtractor a [FeaturesExtractor]
 */
abstract class ActionsScorer<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView,
  out FeaturesType : Features<*, *>>
(
  protected val featuresExtractor: FeaturesExtractor<StateViewType, FeaturesType>
) {

  /**
   * Assign a score to each action of the given list.
   *
   * @param actions a list of actions to score
   */
  abstract fun score(actions: List<Transition<TransitionType, StateType>.Action>)

  /**
   * @return a map of Transitions to their related Actions
   */
  protected fun List<Transition<TransitionType, StateType>.Action>.mapToTransitions():
    Map<Transition<TransitionType, StateType>, List<Transition<TransitionType, StateType>.Action>>
    = this.groupBy { it.transition }
}
