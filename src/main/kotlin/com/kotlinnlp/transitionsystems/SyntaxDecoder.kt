/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

import com.kotlinnlp.transitionsystems.helpers.ActionsGenerator
import com.kotlinnlp.transitionsystems.helpers.BestActionSelector
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.ActionsScorer
import com.kotlinnlp.transitionsystems.state.ItemsContext
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.syntax.DependencyTree

/**
 * The SyntaxDecoder.
 *
 * Processes the input sentence by means of transitions which incrementally build the dependency tree.
 *
 * The algorithm uses a transition-based system. The system is initialized to an initial state based
 * on the input sentence, to which transitions are applied repeatedly generating new states
 * until the final state is reached.
 *
 * @property transitionSystem
 * @property actionsGenerator
 * @property actionsScorer
 * @property bestActionSelector
 * @property verbose
 */
class SyntaxDecoder<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType: ItemsContext<ContextType>>
(
  private val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val actionsGenerator: ActionsGenerator<StateType, TransitionType>,
  private val actionsScorer: ActionsScorer<StateType, TransitionType, *, ContextType, *>,
  private val bestActionSelector: BestActionSelector<StateType, TransitionType>,
  private val verbose: Boolean = false
) {

  /**
   * @param tokens a list of tokens id
   * @param beforeApplyAction callback called before applying the best action (optional)
   *
   * @return a [DependencyTree]
   */
  fun decode(tokens: List<Int>,
             context: ContextType,
             beforeApplyAction: (action: Transition<TransitionType, StateType>.Action) -> Unit = {}): DependencyTree {

    val state = this.transitionSystem.getInitialState(itemIds = tokens)

    while (!state.isTerminal) {

      if (this.verbose) { println(state) }

      val actions = this.actionsGenerator.generateFrom(transitions = this.transitionSystem.getValidTransitions(state))

      this.actionsScorer.score(actions = actions, context = context)

      val bestAction = this.bestActionSelector.select(actions)

      beforeApplyAction(bestAction) // external callback

      this.applyAction(bestAction)
    }

    return state.dependencyTree
  }

  /**
   *
   */
  private fun applyAction(action: Transition<TransitionType, StateType>.Action) {

    if (this.verbose) println("apply: ${action.transition}")

    action.apply()
  }
}
