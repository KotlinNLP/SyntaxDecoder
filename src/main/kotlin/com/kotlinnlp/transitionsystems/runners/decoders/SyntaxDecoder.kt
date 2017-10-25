/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.runners.decoders

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.TransitionSystem
import com.kotlinnlp.transitionsystems.helpers.ActionsGenerator
import com.kotlinnlp.transitionsystems.helpers.BestActionSelector
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.ActionsScorer
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.sortByScoreAndPriority
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.ItemsFactory
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.syntax.DependencyTree

/**
 * The [SyntaxDecoder] decodes the implicit syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * @property transitionSystem a [TransitionSystem]
 * @property itemsFactory the factory of new [StateItem]s
 */
abstract class SyntaxDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>,
  ExtendedStateType : ExtendedState<StateType, TransitionType, ItemType, ContextType>>
(
  protected val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val itemsFactory: ItemsFactory<ItemType>,
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val actionsScorer: ActionsScorer<
    StateType, TransitionType, StateViewType, ContextType, FeaturesType, ItemType, ExtendedStateType>,
  private val bestActionSelector: BestActionSelector<
    StateType, TransitionType, ItemType, ContextType, ExtendedStateType>
) {

  /**
   * Decode the syntax of the given items building a dependency tree.
   *
   * @param itemIds a list of item ids
   * @param context a generic [DecodingContext] used to decode
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a [DependencyTree]
   */
  fun decode(itemIds: List<Int>,
             context: ContextType,
             beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                  extendedState: ExtendedStateType) -> Unit)? = null): DependencyTree {

    @Suppress("UNCHECKED_CAST")
    val extendedState = ExtendedState<StateType, TransitionType, ItemType, ContextType>(
      state = this.transitionSystem.getInitialState(itemIds),
      items = itemIds.map { id -> this.itemsFactory(id) },
      context = context,
      oracle = null) as ExtendedStateType

    return this.processState(extendedState = extendedState, beforeApplyAction = beforeApplyAction)
  }

  /**
   * Decode the syntax starting from an initial state building a dependency tree.
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param beforeApplyAction callback called before applying the best action (optional)
   */
  abstract protected fun processState(extendedState: ExtendedStateType,
                                      beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                                           extendedState: ExtendedStateType) -> Unit)?): DependencyTree

  /**
   * Get the best action to apply, given a [State] and an [ExtendedState].
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   *
   * @return the best action to apply to the given state
   */
  protected fun getBestAction(extendedState: ExtendedStateType): Transition<TransitionType, StateType>.Action {

    val actions: List<Transition<TransitionType, StateType>.Action>
      = this.getScoredActions(extendedState)

    return this.bestActionSelector.select(actions = actions, extendedState = extendedState)
  }

  /**
   * Generate the possible actions allowed in a given state, assigns them a score and returns them in descending order
   * according to the score.
   *
   * @param extendedState the [ExtendedState] context of the state
   *
   * @return a list of Actions
   */
  private fun getScoredActions(extendedState: ExtendedStateType): List<Transition<TransitionType, StateType>.Action> {

    val actions = this.actionsGenerator.generateFrom(
      transitions = this.transitionSystem.generateTransitions(extendedState.state))

    this.actionsScorer.score(actions = actions, extendedState = extendedState)

    @Suppress("UNCHECKED_CAST")
    return actions.sortByScoreAndPriority() as List<Transition<TransitionType, StateType>.Action>
  }
}
