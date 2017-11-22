/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder

import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.modules.actionsscorer.ActionsScorer
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringGlobalSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.FeaturesExtractor
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.bestactionselector.MultiActionsSelector
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.SupportStructuresFactory
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringSupportStructure
import com.kotlinnlp.syntaxdecoder.utils.DaemonThread
import com.kotlinnlp.syntaxdecoder.utils.groupBySize

/**
 * The BeamDecoder decodes the syntax of a list of items building a dependency tree.
 *
 * It uses a transition-based system that evolves an initial state by means of transitions until a final state is
 * reached.
 *
 * More transitions can be applied to a state, following a fixed number of ways in a beam of parallel states. A score is
 * assigned to each state and the one with higher score is chosen as final state.
 *
 * @property beamSize the max size of the beam
 * @property maxParallelThreads the max number of threads that can run in parallel
 * @property transitionSystem a transition system
 * @property actionsGenerator an actions generator
 * @property featuresExtractor a features extractor
 * @property actionsScorer an actions scorer
 * @property multiActionsSelector a multiple actions selector
 * @property supportStructuresFactory a support structures factory
 */
class BeamDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  ScoringGlobalStructureType : ScoringGlobalSupportStructure,
  ScoringStructureType : ScoringSupportStructure<StateType, TransitionType, ContextType, ItemType,
    FeaturesType, ScoringGlobalStructureType>>
(
  val beamSize: Int,
  val maxParallelThreads: Int,
  transitionSystem: TransitionSystem<StateType, TransitionType>,
  actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  featuresExtractor: FeaturesExtractor<StateType, TransitionType, ContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType, ScoringStructureType>,
  actionsScorer: ActionsScorer<StateType, TransitionType, ContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType, ScoringStructureType>,
  val multiActionsSelector: MultiActionsSelector<StateType, TransitionType, ItemType, ContextType>,
  supportStructuresFactory: SupportStructuresFactory<StateType, TransitionType, ContextType, ItemType,
    FeaturesType, ScoringGlobalStructureType, ScoringStructureType>
) :
  SyntaxDecoder<StateType, TransitionType, ContextType, ItemType, FeaturesType, ScoringGlobalStructureType,
    ScoringStructureType>
  (
    transitionSystem = transitionSystem,
    actionsGenerator = actionsGenerator,
    featuresExtractor = featuresExtractor,
    actionsScorer = actionsScorer,
    supportStructuresFactory = supportStructuresFactory
  ) {

  /**
   * A data class used during the selection of the absolute best actions among all the states of the beam.
   *
   * @property futureScore is the score that the state related to the [action] will have if the action is applied or the
   *                       same score of the state at [stateIndex] if it is a final state
   * @property stateIndex the index of the state (related to the [action] if it is not null)
   * @property action is null if 'futureStateScore' is related to a final state
   */
  private data class ActionTriple<StateType : State<StateType>, TransitionType : Transition<TransitionType, StateType>>(
    val futureScore: Double,
    val stateIndex: Int,
    val action: Transition<TransitionType, StateType>.Action?)

  /**
   * A data class used during the selection and the application of the absolute best actions.
   *
   * @property action an action to apply to a state of the beam
   * @property stateIndex the index of the state related to the [action]
   */
  private inner class ActionPair<StateType : State<StateType>, TransitionType : Transition<TransitionType, StateType>>(
    val action: Transition<TransitionType, StateType>.Action,
    val stateIndex: Int)

  /**
   * The array of parallel states of a beam (elements can be null, all by default).
   */
  private val beamStates = arrayOfNulls<ExtendedState<StateType, TransitionType, ItemType, ContextType>>(this.beamSize)

  /**
   * The list beam threads, one per processing state.
   */
  private val beamThreads = List(size = this.beamSize, init = { this.buildThread() })

  /**
   * The list of grouped beam threads.
   */
  private val beamThreadsGroups = this.beamThreads.groupBySize(groupSize = this.maxParallelThreads)

  /**
   * The map of thread ids to the indices of the related states.
   */
  private val threadStateMap = mapOf(*Array(size = this.beamSize, init = { i -> Pair(this.beamThreads[i].id, i) }))

  /**
   * Check the beam size.
   */
  init {
    require(this.beamSize > 0) { "the size of the beam must be >= 1"}
  }

  /**
   * Decode the syntax starting from an initial state building a dependency tree.
   *
   * @param extendedState the [ExtendedState] containing items, context and state
   * @param beforeApplyAction a callback called before applying each best action (optional)
   *
   * @return a dependency tree
   */
  override fun processState(extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
                            beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                                 context: ContextType) -> Unit)?): DependencyTree {

    try {

      this.beamStates[0] = extendedState

      while (this.beamStates.any { it != null && !it.state.isTerminal }) {

        val bestActionsPerState = this.getBestActionsPerState()
        val bestActions = this.selectBestActions(bestActionsPerState)

        this.updateBeam(bestActions = bestActions, beforeApplyAction = beforeApplyAction)
      }

      return this.beamStates.first()!!.state.dependencyTree

    } catch (e: RuntimeException) {
      this.close()
      throw e
    }
  }

  /**
   * Close this decoder killing all beam threads.
   */
  fun close() {
    this.beamThreads.forEach { it.interrupt() }
  }

  /**
   * @return a new [BeamDecoderThread] already started.
   */
  private fun buildThread(): BeamDecoderThread<StateType, TransitionType, ContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType, ScoringStructureType> {

    val thread = BeamDecoderThread(
      transitionSystem = this.transitionSystem,
      actionsGenerator = this.actionsGenerator,
      featuresExtractor = this.featuresExtractor,
      actionsScorer = this.actionsScorer,
      multiActionsSelector = this.multiActionsSelector,
      supportStructuresFactory = this.supportStructuresFactory
    )

    thread.start()

    return thread
  }

  /**
   * Get an array of best actions per state.
   * It is parallel to the beam states and at each index contains the best actions of the related state or null if the
   * state has no candidate best actions-
   *
   * @return an array containing a list of actions or null as elements, parallel to the beam states list
   */
  private fun getBestActionsPerState(): Array<List<Transition<TransitionType, StateType>.Action>?> {

    val scoreThreshold: Double? = this.getScoreThreshold()
    val bestActionsPerState = arrayOfNulls<List<Transition<TransitionType, StateType>.Action>>(this.beamSize)

    this.beamThreadsGroups.forEach { tGroup ->

      tGroup.forEachNotNullState { _, state, thread ->
        thread.write(BeamDecoderThreadInput(extendedState = state, scoreThreshold = scoreThreshold))
      }

      tGroup.forEachNotNullState { stateIndex, _, thread ->
        bestActionsPerState[stateIndex] = thread.read()
      }
    }

    return bestActionsPerState
  }

  /**
   * @return the score threshold based on the lowest beam state log score (null if the beam is not already full)
   */
  private fun getScoreThreshold(): Double? =
    if (this.beamStates.any { it == null }) null else this.beamStates.last { it != null }!!.logScore

  /**
   * Select the best actions that will generate the next states of the beam.
   *
   * @param bestActionsPerState an array containing a list of actions or null as elements, parallel to the beam states
   *                            list
   *
   * @return the best actions to apply to the current beam states, as list of action pairs
   */
  private fun selectBestActions(bestActionsPerState: Array<List<Transition<TransitionType, StateType>.Action>?>)
    : List<ActionPair<StateType, TransitionType>> {

    val actionTriples = mutableListOf<ActionTriple<StateType, TransitionType>>()

    bestActionsPerState.forEachIndexed { stateIndex, actions ->
      if (actions != null) {
        val state: ExtendedState<StateType, TransitionType, ItemType, ContextType> = this.beamStates[stateIndex]!!
        actionTriples.addAll(actions.map {
          ActionTriple(futureScore = state.simulateFutureScore(it), stateIndex = stateIndex, action = it)
        })
      }
    }

    this.beamStates.forEachTerminal { stateIndex, state -> // add terminal states to consider them in the sorting
      actionTriples.add(ActionTriple(futureScore = state.logScore, stateIndex = stateIndex, action = null))
    }

    actionTriples.sortByDescending { it.futureScore }

    return actionTriples
      .subList(0, minOf(actionTriples.size, this.beamSize))
      .filter { it.action != null } // remove states already terminal (will be added in the updateBeam method)
      .map { ActionPair(action = it.action!!, stateIndex = it.stateIndex) }
  }

  /**
   * Update the beam replacing the old states with the ones obtained applying the given actions.
   *
   * @param bestActions the best actions to apply to the current beam states, as action pairs
   * @param beforeApplyAction a callback called before applying each best action (optional)
   */
  private fun updateBeam(bestActions: List<ActionPair<StateType, TransitionType>>,
                         beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                                              context: ContextType) -> Unit)?) {

    val terminalStates = this.beamStates.filter { it != null && it.state.isTerminal }.map { it!! }
    val newStates = bestActions.map { it.applyAndBuildNewState(beforeApplyAction) }
    val replacingStates = (terminalStates + newStates).sortedByDescending { it.logScore }

    this.replaceBeamStates(states = replacingStates.subList(0, minOf(this.beamSize, replacingStates.size)))
  }

  /**
   * Replace the beam states with the given [states] (already ordered), setting the remaining beam elements to null.
   *
   * @param states the states to replace in the current beam
   */
  private fun replaceBeamStates(states: List<ExtendedState<StateType, TransitionType, ItemType, ContextType>>) {
    assert(states.size <= this.beamStates.size)

    (0 until this.beamStates.size).forEach { i ->
      this.beamStates[i] = if (i < states.size) states[i] else null
    }
  }

  /**
   * Loop extended states containing terminal states, with index.
   */
  private fun Array<ExtendedState<StateType, TransitionType, ItemType, ContextType>?>.forEachTerminal(
    callback: (stateIndex: Int, state: ExtendedState<StateType, TransitionType, ItemType, ContextType>) -> Unit
  ) {
    this.forEachIndexed { stateIndex, state ->
      if (state != null && state.state.isTerminal) {
        callback(stateIndex, state)
      }
    }
  }

  /**
   * Apply this action and build a new extended state containing the updated copy of the action state.
   *
   * @param beforeApplyAction callback called before applying this action (optional)
   *
   * @return a new extended state
   */
  private fun ActionPair<StateType, TransitionType>.applyAndBuildNewState(
    beforeApplyAction: ((action: Transition<TransitionType, StateType>.Action,
                         context: ContextType) -> Unit)?
  ): ExtendedState<StateType, TransitionType, ItemType, ContextType> {

    val newState: StateType = this.action.apply(copyState = true)
    val newExtendedState = this@BeamDecoder.beamStates[this.stateIndex]!!.clone(state = newState)

    newExtendedState.accumulateScore(this.action.score)

    beforeApplyAction?.invoke(this.action, newExtendedState.context) // external callback

    return newExtendedState
  }

  /**
   * Loop threads and related states (with index), only whose state is not null.
   */
  private fun List<BeamDecoderThread<StateType, TransitionType, ContextType, ItemType, FeaturesType,
    ScoringGlobalStructureType, ScoringStructureType>>.forEachNotNullState(
    callback: (stateIndex: Int,
               state: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
               thread: BeamDecoderThread<StateType, TransitionType, ContextType, ItemType, FeaturesType,
                 ScoringGlobalStructureType, ScoringStructureType>) -> Unit) {

    this.forEach { thread ->

      val stateIndex: Int = thread.getStateIndex()
      val state: ExtendedState<StateType, TransitionType, ItemType, ContextType>?
        = this@BeamDecoder.beamStates[stateIndex]

      if (state != null) {
        callback(stateIndex, state, thread)
      }
    }
  }

  /**
   * @return the index of the state related to this thread
   */
  private fun DaemonThread<*, *>.getStateIndex(): Int = this@BeamDecoder.threadStateMap[this.id]!!
}
