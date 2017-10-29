/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.progressindicator.ProgressIndicatorBar
import com.kotlinnlp.syntaxdecoder.*
import com.kotlinnlp.syntaxdecoder.helpers.ActionsGenerator
import com.kotlinnlp.syntaxdecoder.state.State

/**
 *
 */
class TransitionSystemCoverage<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>(
  private val transitionSystem: TransitionSystem<StateType, TransitionType>,
  private val oracle: Oracle<StateType, TransitionType>,
  private val errorExploring: Boolean,
  private val verbose: Boolean = false){

  init {
    require(!this.errorExploring || this.oracle.type == Oracle.Type.DYNAMIC) {
      "error-exploring not supported by ${this.oracle.type} oracle."
    }
  }

  /**
   *
   */
  private val actionsGenerator = ActionsGenerator.Unlabeled<StateType, TransitionType>()

  /**
   *
   */
  fun testCoverage(sentences: ArrayList<Sentence>) {

    val progress = ProgressIndicatorBar(sentences.size)

    sentences.forEach {
      progress.tick()
      this.compute(it)
    }
  }

  /**
   *
   */
  private fun compute(sentence: Sentence) {

    this.oracle.initialize(sentence.dependencyTree!!)

    val state = this.transitionSystem.getInitialState(itemIds = sentence.tokens)

    while (!state.isTerminal){
      if (this.verbose) { println(state) }

      val possibleActions = this.generatePossibleActions(state)
      val actionToApply = this.chooseAction(possibleActions)

      this.applyAction(actionToApply)
    }

    if (!this.errorExploring) {
      require(state.dependencyTree.matchStructure(sentence.dependencyTree)) {
        println("Found:\n${state.dependencyTree}\n\nExpected:\n${sentence.dependencyTree}")
      }
    }
  }

  /**
   *
   */
  private fun generatePossibleActions(state: StateType): List<Transition<TransitionType, StateType>.Action>{

    val transitions: List<TransitionType> = this.transitionSystem.generateTransitions(state)

    return this.actionsGenerator.generateFrom(transitions)
  }

  /**
   *
   */
  private fun chooseAction(actions: List<Transition<TransitionType, StateType>.Action>):
    Transition<TransitionType, StateType>.Action {

    require(actions.any { this.oracle.hasZeroCost(it) }) { "there should always be a 0 cost action" }

    return actions.let {
      if (errorExploring) it.getRandomAction() else it.first {  this.oracle.isCorrect(it) }
    }
  }

  /**
   *
   */
  private fun applyAction(action: Transition<TransitionType, StateType>.Action){

    if (this.verbose) println("apply: ${action.transition}")

    this.oracle.updateWith(action.transition) // important

    action.apply()
  }

  /**
   *
   */
  private fun List<Transition<TransitionType, StateType>.Action>.getRandomAction():
    Transition<TransitionType, StateType>.Action {

    return this[Math.round(Math.random() * this.lastIndex).toInt()]
  }
}
