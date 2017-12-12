/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.syntaxdecoder.transitionsystem.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionSystem
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.Oracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.oracle.OracleFactory

/**
 *
 */
class TransitionSystemCoverage<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>(
  private val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val oracleFactory: OracleFactory<StateType, TransitionType>,
  private val errorExploring: Boolean,
  private val verbose: Boolean = false){

  /**
   *
   */
  private val actionsGenerator = ActionsGenerator.Unlabeled<StateType, TransitionType>()

  /**
   *
   */
  fun run(sentence: Sentence) {

    val oracle = oracleFactory(sentence.dependencyTree!!)

    require(!this.errorExploring || oracle.type == Oracle.Type.DYNAMIC) {
      "error-exploring not supported by ${oracle.type} oracle."
    }

    val state = this.transitionSystem.getInitialState(itemIds = sentence.tokens, size = sentence.tokens.size)

    while (!state.isTerminal){

      if (this.verbose) { println(state) }

      val bestAction = this.getBestAction(state = state, oracle = oracle)

      if (this.verbose) println("apply: ${bestAction.transition}")

      oracle.apply(bestAction.transition) // important
      bestAction.apply()
    }

    if (!this.errorExploring) {
      require(state.dependencyTree.matchHeads(sentence.dependencyTree.heads)) {
        println("Found:\n${state.dependencyTree}\n\nExpected:\n${sentence.dependencyTree}")
      }
    }
  }

  /**
   *
   */
  private fun getBestAction(state: StateType, oracle: Oracle<StateType, TransitionType>):
    Transition<TransitionType, StateType>.Action {

    val actions = this.actionsGenerator.generateFrom(this.transitionSystem.generateTransitions(state))

    require(actions.any { it.transition.isAllowed && oracle.hasZeroCost(it) }) {
      "there should always be a 0 cost action"
    }

    return if (this.errorExploring){
      val allowedActions = actions.filter { it.transition.isAllowed }
      allowedActions[Math.round(Math.random() * allowedActions.lastIndex).toInt()]
    } else {
      actions.first {  it.transition.isAllowed && oracle.isCorrect(it) }
    }
  }
}
