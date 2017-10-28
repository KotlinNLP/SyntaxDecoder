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
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.ActionsScorerStructure
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.FeaturesExtractor
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.FeaturesExtractorStructure
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
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>,
  FeaturesExtractorStructureType: FeaturesExtractorStructure<
    FeaturesExtractorStructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>,
  ActionsScorerStructureType: ActionsScorerStructure<
    ActionsScorerStructureType, StateType, TransitionType, ContextType, ItemType>>
(
  protected val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val itemsFactory: ItemsFactory<ItemType>,
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val featuresExtractor: FeaturesExtractor<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
  private val actionsScorer: ActionsScorer<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, ActionsScorerStructureType>,
  private val bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>
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
  fun decode(
    itemIds: List<Int>,
    context: ContextType,
    beforeApplyAction: ((
      action: Transition<TransitionType, StateType>.Action,
      extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)? = null
  ): DependencyTree {

    val extendedState = ExtendedState<StateType, TransitionType, ItemType, ContextType>(
      state = this.transitionSystem.getInitialState(itemIds),
      items = itemIds.map { id -> this.itemsFactory(id) },
      context = context,
      oracle = null)

    return this.processState(extendedState = extendedState, beforeApplyAction = beforeApplyAction)
  }

  /**
   * Decode the syntax starting from an initial state building a dependency tree.
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a dependency tree
   */
  abstract protected fun processState(
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
    beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                         extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)?
  ): DependencyTree

  /**
   * Get the best action to apply, given a [State] and an [ExtendedState].
   *
   * @param featuresExtractorStructure the support structure of the [featuresExtractor]
   * @param actionsScorerStructure the support structure of the [actionsScorer]
   * @param extendedState the [ExtendedState] containing items, context and state
   *
   * @return the best action to apply to the given state
   */
  protected fun getBestAction(
    featuresExtractorStructure: FeaturesExtractorStructureType,
    actionsScorerStructure: ActionsScorerStructureType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ): Transition<TransitionType, StateType>.Action {

    val scoredActions: List<Transition<TransitionType, StateType>.Action> = this.getScoredActions(
      featuresExtractorStructure = featuresExtractorStructure,
      actionsScorerStructure = actionsScorerStructure,
      extendedState = extendedState)

    return this.bestActionSelector.select(actions = scoredActions, extendedState = extendedState)
  }

  /**
   * Generate the possible actions allowed in a given state, assigns them a score and returns them in descending order
   * according to the score.
   *
   * @param featuresExtractorStructure the support structure of the [featuresExtractor]
   * @param actionsScorerStructure the support structure of the [actionsScorer]
   * @param extendedState the [ExtendedState] containing items, context and state
   *
   * @return a list of scored actions
   */
  private fun getScoredActions(
    featuresExtractorStructure: FeaturesExtractorStructureType,
    actionsScorerStructure: ActionsScorerStructureType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ): List<Transition<TransitionType, StateType>.Action> {

    val actions = this.actionsGenerator.generateFrom(
      transitions = this.transitionSystem.generateTransitions(extendedState.state))

    val actionsDynamicStructure = actionsScorerStructure.dynamicStructureFactory(
      actions = actions,
      extendedState = extendedState)

    val featuresDynamicStructure = featuresExtractorStructure.dynamicStructureFactory(
      extendedState = extendedState,
      stateView = this.actionsScorer.buildStateView(actionsDynamicStructure))

    this.featuresExtractor.setFeatures(featuresDynamicStructure)

    this.actionsScorer.score(features = featuresDynamicStructure.features, structure = actionsDynamicStructure)

    return actions.sortByScoreAndPriority()
  }
}
