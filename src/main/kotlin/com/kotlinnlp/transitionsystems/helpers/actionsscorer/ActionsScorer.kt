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
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
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
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>,
  ExtendedStateType : ExtendedState<ExtendedStateType, StateType, ItemType, ContextType>>
(
  protected val featuresExtractor: FeaturesExtractor<StateViewType, ContextType, FeaturesType>
) {

  /**
   * Assign scores to the given [actions] using the [extendedState].
   *
   * @param actions a list of actions to score
   * @param extendedState the extended state containing items, context and state
   */
  open fun score(actions: List<Transition<TransitionType, StateType>.Action>, extendedState: ExtendedStateType) {

    this.assignScore(actions = actions, extendedState = extendedState)
  }

  /**
   * Abstract fun that assign a score to each action of the given list.
   *
   * @param actions a list of actions to score
   * @param extendedState the extended state containing items, context and state
   */
  abstract protected fun assignScore(actions: List<Transition<TransitionType, StateType>.Action>,
                                     extendedState: ExtendedStateType)

  /**
   * @return a map of Transitions to their related Actions
   */
  protected fun List<Transition<TransitionType, StateType>.Action>.mapToTransitions():
    Map<Transition<TransitionType, StateType>, List<Transition<TransitionType, StateType>.Action>>
    = this.groupBy { it.transition }
}
