/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems

/**
 * The State Transition.
 *
 * @property state the [State] on which this transition operates.
 */
abstract class Transition<SelfType: Transition<SelfType, StateType>, StateType: State<StateType>>(
  val state: StateType
) {

  /**
   * The Action.
   *
   * Transition abstraction that allows you to ignore which transition-system you are using.
   */
  inner abstract class Action(val id: Int = -1, var score: Double) {

    /**
     * @return the state on which this action operates.
     */
    val state: StateType = this@Transition.state

    /**
     * @return the [Transition] from which this [Action] originated.
     */
    @Suppress("UNCHECKED_CAST")
    val transition: SelfType = this@Transition as SelfType

    /**
     * @param cloneState whether to apply this action to a copy of the [State] of this action.
     *
     * @return the current state (or a new one if cloned) after the execution of this action.
     */
    fun apply(cloneState: Boolean = false): StateType {

      val state = this@Transition.apply(cloneState)

      this.perform(state) // call after the transition has been performed

      return state
    }

    /**
     * @param state the state on which to apply this action
     */
    abstract protected fun perform(state: StateType)
  }

  /**
   * Shift Action.
   */
  inner class Shift(id: Int, score: Double) : Action(id, score) {

    /**
     * @param state the state on which to apply this action.
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation.
     */
    override fun toString(): String = "shift"
  }

  /**
   * Unshift Action.
   */
  inner class Unshift(id: Int, score: Double) : Action(id, score) {

    /**
     * @param state the state on which to apply this action.
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation.
     */
    override fun toString(): String = "unshift"
  }

  /**
   * Relocate Action (can be used to abstract the Swap transition).
   */
  inner class Relocate(id: Int, score: Double) : Action(id, score) {

    /**
     * @param state the state on which to apply this action.
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation.
     */
    override fun toString(): String = "relocate"
  }

  /**
   * NoArc Action.
   */
  inner class NoArc(id: Int, score: Double) : Action(id, score) {

    /**
     * @param state the state on which to apply this action.
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation.
     */
    override fun toString(): String = "no-arc"
  }

  /**
   * Arc Action.
   */
  inner class Arc(id: Int, score: Double) : Action(id, score), DependencyRelation {

    /**
     * The dependent id.
     */
    override val dependentId: Int

    /**
     * The governor id (can be null in case the governor is the root).
     */
    override val governorId: Int?

    /**
     * The Dependency Relation (can be null)
     */
    override var deprel: Deprel? = null

    /**
     * Initialize the action.
     */
    init {
      require(this@Transition is SyntacticDependency)

      this@Transition as SyntacticDependency

      this.dependentId = this@Transition.dependentId
      this.governorId = this@Transition.governorId
    }

    /**
     * @param state the state on which to apply this action
     */
    override fun perform(state: StateType) {
      state.dependencyTree.setArc(
        dependentId = this.dependentId,
        governorId = this.governorId,
        deprel = deprel)
    }

    /**
     * @return its string representation.
     */
    override fun toString(): String = "${deprel?:"arc"}(${this.governorId} -> ${this.dependentId})"
  }

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  abstract val priority: Int

  /**
   *  True if the transition is allowed in the given parser state.
   */
  abstract val isAllowed: Boolean get

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Action] tied to this transition.
   */
  fun actionFactory(id: Int = -1, score: Double = 0.0, deprel: Deprel? = null): Action {

    val action: Action = this.buildAction(id, score)

    if (this is SyntacticDependency) {
      require(action is DependencyRelation) { "An arc Transition must be associated to a DependencyRelation" }
      (action as DependencyRelation).deprel = deprel
    }

    return action
  }

  /**
   * Apply this transition on a given [state].
   *
   * @param cloneState a Boolean indicating if the transition must be applied to a clone of the [state]
   *
   * @return the modified state or a new one if [cloneState] is true
   */
  internal fun apply(cloneState: Boolean = false): StateType {

    val state: StateType = if (cloneState) this.state.clone() else this.state

    this.perform(state)

    return state
  }

  /**
   * Apply this transition on a given [state].
   * It requires that the transition [isAllowed] on the given [state].
   *
   * @param state the state on which to apply this transition.
   */
  abstract protected fun perform(state: StateType)

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Action] tied to this transition.
   */
  abstract protected fun buildAction(id: Int = -1, score: Double = 0.0): Action

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Shift] tied to this transition.
   */
  protected fun buildShift(id: Int = -1, score: Double = 0.0) = this.Shift(id = id, score = score)

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Unshift] tied to this transition.
   */
  protected fun buildUnshift(id: Int = -1, score: Double = 0.0) = this.Unshift(id = id, score = score)

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Relocate] tied to this transition.
   */
  protected fun buildRelocate(id: Int = -1, score: Double = 0.0) = this.Relocate(id = id, score = score)

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Arc] tied to this transition.
   */
  protected fun buildNoArc(id: Int = -1, score: Double = 0.0) = this.NoArc(id = id, score = score)

  /**
   * @param id the id of the action
   * @param score the score of the action
   *
   * @return a new [Arc] tied to this transition.
   */
  protected fun buildArc(id: Int = -1, score: Double = 0.0) = this.Arc(id = id, score = score)
}
