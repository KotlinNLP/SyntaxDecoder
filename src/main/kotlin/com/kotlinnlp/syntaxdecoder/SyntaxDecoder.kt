/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.BestActionSelector
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorer
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorerStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractorStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.syntax.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState

/**
 * The [SyntaxDecoder] decodes the implicit syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * @property transitionSystem a [TransitionSystem]
 * @property actionsGenerator an actions generator
 * @property featuresExtractor a features extractor
 * @property actionsScorer an actions scorer
 * @property bestActionSelector a best action selector
 */
abstract class SyntaxDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>,
  FeaturesExtractorStructureType : FeaturesExtractorStructure<
    FeaturesExtractorStructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>,
  ActionsScorerStructureType : ActionsScorerStructure<
    ActionsScorerStructureType, StateType, TransitionType, ContextType, ItemType>>
(
  val transitionSystem: TransitionSystem<StateType, TransitionType>,
  val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  val featuresExtractor: FeaturesExtractor<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, FeaturesExtractorStructureType>,
  val actionsScorer: ActionsScorer<
    StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, ActionsScorerStructureType>,
  val bestActionSelector: BestActionSelector<StateType, TransitionType, ItemType, ContextType>
) {

  /**
   * Decode the syntax of the given items building a dependency tree.
   *
   * @param context a generic [DecodingContext] used to decode
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a [DependencyTree]
   */
  fun decode(
    context: ContextType,
    beforeApplyAction: ((
      action: Transition<TransitionType, StateType>.Action,
      extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit)? = null
  ): DependencyTree {

    val extendedState = ExtendedState<StateType, TransitionType, ItemType, ContextType>(
      state = this.transitionSystem.getInitialState(context.items.map { it.id }),
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
  protected fun getScoredActions(
    featuresExtractorStructure: FeaturesExtractorStructureType,
    actionsScorerStructure: ActionsScorerStructureType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ): List<Transition<TransitionType, StateType>.Action> {

    val actionsScorerMemory = actionsScorerStructure.buildMemoryOf(
      actions = this.actionsGenerator.generateFrom(
        transitions = this.transitionSystem.generateTransitions(extendedState.state)),
      extendedState = extendedState)

    val featuresExtractorMemory = featuresExtractorStructure.buildMemoryOf(
      extendedState = extendedState,
      stateView = this.actionsScorer.buildStateView(actionsScorerMemory))

    this.featuresExtractor.setFeatures(featuresExtractorMemory)

    this.actionsScorer.score(features = featuresExtractorMemory.features, structure = actionsScorerMemory)

    return actionsScorerMemory.sortedActions
  }
}
