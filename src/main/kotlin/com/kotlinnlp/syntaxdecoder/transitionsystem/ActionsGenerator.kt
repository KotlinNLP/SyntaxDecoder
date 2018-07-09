/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem

import com.google.common.collect.HashMultimap
import com.kotlinnlp.dependencytree.Deprel
import com.kotlinnlp.dependencytree.POSTag
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency

/**
 * The ActionsGenerator.
 */
sealed class ActionsGenerator<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>> {

  /**
   * Generate the possible actions allowed in a given state.
   *
   * @param transitions the transitions from which to generate the actions
   *
   * @return a list of Actions
   */
  fun generateFrom(transitions: List<TransitionType>):
    List<Transition<TransitionType, StateType>.Action> {

    val result = arrayListOf<Transition<TransitionType, StateType>.Action>()

    transitions.forEach { result.addAll(it.generateActions(startId = result.size)) }

    return result
  }

  /**
   * @param startId the first available id (incremental) to assign to the generated actions
   *
   * @return a list of Actions
   */
  protected abstract fun Transition<TransitionType, StateType>.generateActions(startId: Int):
    List<Transition<TransitionType, StateType>.Action>

  /**
   * The Unlabeled Actions Generator generates an action for each transition, resulting in a 1:1
   * transition-action relation.
   */
  class Unlabeled<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>
    : ActionsGenerator<StateType, TransitionType>() {

    /**
     * @param startId the first available id (incremental) to assign to the generated actions
     *
     * @return a list of Actions
     */
    override fun Transition<TransitionType, StateType>.generateActions(startId: Int):
      List<Transition<TransitionType, StateType>.Action>
      = listOf(this.actionFactory(id = startId))
  }

  /**
   * The Labeled Actions Generator can generate multiple actions for each transition, resulting in a 1:N
   * transition-actions relation, where N > 1 in case of transitions that create a syntactic dependency
   * and N is the number of 'deprels' associated to the transition direction (left, right, root).
   */
  class Labeled<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>(
    private val deprels: Map<Deprel.Position, List<Deprel>>
  ) : ActionsGenerator<StateType, TransitionType>(){

    /**
     * @return a list of Actions
     */
    override fun Transition<TransitionType, StateType>.generateActions(startId: Int):
      List<Transition<TransitionType, StateType>.Action> {

      val actions = mutableListOf<Transition<TransitionType, StateType>.Action>()
      var actionId = startId

      if (this is SyntacticDependency && this.type.direction in this@Labeled.deprels) {

        this@Labeled.deprels.getValue(this.type.direction).forEach { deprel ->
          actions.add(this.actionFactory(id = actionId++, deprel = deprel))
        }

      } else {
        actions.add(this.actionFactory(id = actionId++))
      }

      return actions
    }
  }

  /**
   * The Labeled Actions Generator can generate multiple actions for each transition, resulting in a 1:N
   * transition-actions relation, where N > 1 in case of transitions that create a syntactic dependency
   * and N is the number of [deprelPosTagCombinations].
   */
  class MorphoSyntacticLabeled<StateType: State<StateType>, TransitionType: Transition<TransitionType, StateType>>(
    private val deprels: Map<Deprel.Position, List<Deprel>>,
    private val deprelPosTagCombinations: HashMultimap<Deprel, POSTag>
  ) : ActionsGenerator<StateType, TransitionType>(){

    /**
     * @return a list of Actions
     */
    override fun Transition<TransitionType, StateType>.generateActions(startId: Int):
      List<Transition<TransitionType, StateType>.Action> {

      val actions = mutableListOf<Transition<TransitionType, StateType>.Action>()
      var actionId = startId

      if (this is SyntacticDependency && this.type.direction in this@MorphoSyntacticLabeled.deprels) {

        this@MorphoSyntacticLabeled.deprels.getValue(this.type.direction).forEach { deprel ->
          this@MorphoSyntacticLabeled.deprelPosTagCombinations.get(deprel).forEach { posTag ->

            actions.add(this.actionFactory(id = actionId++, deprel = deprel, posTag = posTag))
          }
        }

      } else {
        actions.add(this.actionFactory(id = actionId++))
      }

      return actions
    }
  }
}
