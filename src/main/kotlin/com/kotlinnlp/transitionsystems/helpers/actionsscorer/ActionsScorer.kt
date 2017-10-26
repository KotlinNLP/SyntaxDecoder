/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.resetScores
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * The ActionsScorer.
 *
 * @property featuresExtractor a [FeaturesExtractor]
 */
abstract class ActionsScorer<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView<StateType>,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>>
(
  protected val featuresExtractor: FeaturesExtractor<
    StateType, TransitionType, ItemType, ContextType, StateViewType, FeaturesType>
) {

  /**
   * Contains the last scored actions.
   */
  protected lateinit var lastScoredActions: List<Transition<TransitionType, StateType>.Action>

  /**
   * Assign scores to the given [actions] using the [extendedState] as context.
   *
   * @param actions a list of actions to score
   * @param extendedState the extended state containing items, context and state
   */
  fun score(actions: List<Transition<TransitionType, StateType>.Action>,
            extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) {

    actions.resetScores()

    this.lastScoredActions = actions

    this.assignScore(actions = actions, extendedState = extendedState)
  }

  /**
   * Assign scores to the given [actions] using the [extendedState] as context.
   *
   * @param actions a list of actions to score
   * @param extendedState the extended state containing items, context and state
   */
  abstract fun assignScore(actions: List<Transition<TransitionType, StateType>.Action>,
                           extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>)

  /**
   * @return a map of Transitions to their related Actions
   */
  protected fun List<Transition<TransitionType, StateType>.Action>.mapToTransitions():
    Map<Transition<TransitionType, StateType>, List<Transition<TransitionType, StateType>.Action>>
    = this.groupBy { it.transition }
}
