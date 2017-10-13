/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

import com.kotlinnlp.transitionsystems.helpers.ActionsGenerator
import com.kotlinnlp.transitionsystems.helpers.BestActionSelector
import com.kotlinnlp.transitionsystems.helpers.TransitionsGenerator
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.ActionsScorer
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem
import kotlin.reflect.KClass

/**
 * The TransitionSystem.
 */
abstract class TransitionSystem<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>,
  ExtendedStateType : ExtendedState<ExtendedStateType, StateType, ItemType, ContextType>>
(
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val actionsScorer: ActionsScorer<
    StateType, TransitionType, StateViewType, ContextType, FeaturesType, ItemType, ExtendedStateType>,
  private val bestActionSelector: BestActionSelector<StateType, TransitionType>
) {

  /**
   * The [KClass] of the StateType used in the [getInitialState] function.
   */
  abstract protected val stateClass: KClass<StateType>

  /**
   * The [TransitionsGenerator] used to generate the next valid transitions given a [State].
   */
  abstract protected val transitionsGenerator: TransitionsGenerator<StateType, TransitionType>

  /**
   * Initialization function, mapping an ordered list of [itemIds] to an initial state.
   *
   * @property itemIds the list of item ids used to initialize the state
   *
   * @return a new initialized [State]
   */
  fun getInitialState(itemIds: List<Int>): StateType = this.stateClass.constructors.first().call(itemIds)

  /**
   * Get the best action to apply, given a [State] and an [ExtendedState].
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   *
   * @return the best action to apply to the given state
   */
  fun getBestAction(extendedState: ExtendedStateType): Transition<TransitionType, StateType>.Action {

    val actions = this.actionsGenerator.generateFrom(
      transitions = this.transitionsGenerator.generate(extendedState.state))

    this.actionsScorer.score(actions = actions, extendedState = extendedState)

    return this.bestActionSelector.select(actions)
  }
}
