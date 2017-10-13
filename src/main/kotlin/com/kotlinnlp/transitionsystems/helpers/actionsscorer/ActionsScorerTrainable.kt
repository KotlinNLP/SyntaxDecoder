/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * The ActionsScorer that implements [Trainable].
 *
 * @property featuresExtractor a [FeaturesExtractor]
 */
abstract class ActionsScorerTrainable<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>,
  ExtendedStateType : ExtendedState<ExtendedStateType, StateType, ItemType, ContextType>>
(
  featuresExtractor: FeaturesExtractor<StateViewType, ContextType, FeaturesType>
) :
  ActionsScorer<
    StateType,
    TransitionType,
    StateViewType,
    ContextType,
    FeaturesType,
    ItemType,
    ExtendedStateType>(featuresExtractor),
  Trainable {

  /**
   * The last scored actions.
   */
  lateinit private var lastScoredActions: List<Transition<TransitionType, StateType>.Action>

  /**
   * The [ExtendedState] of the last scored actions.
   */
  lateinit private var lastExtendedState: ExtendedStateType

  /**
   * Assign scores to the given [actions] using the [extendedState] and save them into [lastScoredActions] and
   * [lastExtendedState].
   *
   * @param actions a list of actions to score
   * @param extendedState the extended state containing items, context and state
   */
  override fun score(actions: List<Transition<TransitionType, StateType>.Action>, extendedState: ExtendedStateType) {

    this.assignScore(actions = actions, extendedState = extendedState)

    this.lastScoredActions = actions
    this.lastExtendedState = extendedState
  }

  /**
   * Set the 'error' property of the last scored actions.
   */
  fun setErrors() {
    this.assignErrors(actions = this.lastScoredActions, extendedState = this.lastExtendedState)
  }

  /**
   * Assign errors to the last scored actions.
   *
   * @param actions a list with the last scored actions
   * @param extendedState the extended state of the last scored actions
   */
  abstract fun assignErrors(actions: List<Transition<TransitionType, StateType>.Action>,
                            extendedState: ExtendedStateType)
}
