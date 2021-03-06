/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem

import com.kotlinnlp.linguisticdescription.GrammaticalConfiguration
import com.kotlinnlp.syntaxdecoder.syntax.DependencyRelation
import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.linguisticdescription.syntax.SyntacticDependency.Direction

/**
 * The State Transition.
 *
 * @property refState the [State] on which this transition operates
 * @property id the transition id
 */
abstract class Transition<SelfType: Transition<SelfType, StateType>, StateType: State<StateType>>(
  val refState: StateType,
  val id: Int
) {

  /**
   * The Transition type.
   *
   * @property direction
   */
  enum class Type(val direction: Direction) {
    SHIFT(direction = Direction.NULL),
    RELOCATE(direction = Direction.NULL),
    WAIT(direction = Direction.NULL),
    UNSHIFT(direction = Direction.NULL),
    NO_ARC(direction = Direction.NULL),
    ARC_LEFT(direction = Direction.LEFT),
    ARC_RIGHT(direction = Direction.RIGHT),
    ROOT(direction = Direction.ROOT)
  }

  /**
   * The Action is a transition abstraction that allows you to ignore which transition-system you are using.
   *
   * @property id a unique id that identifies this action within others generated at the same time
   */
  abstract inner class Action internal constructor(val id: Int = -1) {

    /**
     * The [Transition] from which this [Action] originated.
     */
    @Suppress("UNCHECKED_CAST")
    val transition: SelfType = this@Transition as SelfType

    /**
     *  True if the transition is allowed in the given parser state.
     */
    val isAllowed: Boolean by lazy { this.transition.isAllowed }

    /**
     * The score of goodness of this action.
     */
    var score: Double = 0.0

    /**
     * The error of the [score].
     */
    var error: Double = 0.0

    /**
     * Apply this [Action] to the [refState] or a copy of it. Update the 'track' of the involved state.
     *
     * @param copyState whether perform this [Action] to the [refState] or a copy of it
     *
     * @return the state modified by this [Action]
     */
    fun apply(copyState: Boolean = false): StateType {

      require(this@Transition.refStateTrack == this@Transition.refState.track.get()){
        "Incompatible state track: " +
          "Expected: ${this@Transition.refStateTrack} Found: ${this@Transition.refState.track.get()}"
      }

      val state: StateType = this.transition.refState.let { if (copyState) it.copy() else it }

      this@Transition.perform(state)

      this.perform(state) // call after the transition has been performed

      state.track.incrementAndGet()

      return state
    }

    /**
     * Perform this [Action] modifying the DependencyTree of the given [state].
     * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
     * compatible with this [Action] as it can only be the [refState] or a copy of it.
     *
     * @param state a State
     */
    protected abstract fun perform(state: StateType)
  }

  /**
   * Shift Action.
   */
  inner class Shift internal constructor(id: Int) : Action(id) {

    /**
     * Perform this [Action] modifying the DependencyTree of the given [state].
     * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
     * compatible with this [Action] as it can only be the [refState] or a copy of it.
     *
     * @param state a State
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation
     */
    override fun toString(): String = "shift"
  }

  /**
   * Unshift Action.
   */
  inner class Unshift internal constructor(id: Int) : Action(id) {

    /**
     * Perform this [Action] modifying the DependencyTree of the given [state].
     * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
     * compatible with this [Action] as it can only be the [refState] or a copy of it.
     *
     * @param state a State
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation
     */
    override fun toString(): String = "unshift"
  }

  /**
   * Relocate Action (can be used to abstract the Swap transition).
   */
  inner class Relocate internal constructor(id: Int) : Action(id) {

    /**
     * Perform this [Action] modifying the DependencyTree of the given [state].
     * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
     * compatible with this [Action] as it can only be the [refState] or a copy of it.
     *
     * @param state a State
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation
     */
    override fun toString(): String = "relocate"
  }

  /**
   * NoArc Action.
   */
  inner class NoArc internal constructor(id: Int) : Action(id) {

    /**
     * Perform this [Action] modifying the DependencyTree of the given [state].
     * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
     * compatible with this [Action] as it can only be the [refState] or a copy of it.
     *
     * @param state a State
     */
    override fun perform(state: StateType) = Unit

    /**
     * @return its string representation
     */
    override fun toString(): String = "no-arc"
  }

  /**
   * Arc Action.
   */
  inner class Arc internal constructor(id: Int) : Action(id), DependencyRelation {

    /**
     * The dependent id (can be null in case the transition is not allowed).
     */
    override var dependentId: Int? = null
      private set

    /**
     * The governor id (can be null in case the transition is not allowed or the governor is the root).
     */
    override var governorId: Int? = null
      private set

    /**
     * The morpho-syntactic component of a dependency relation (can be null).
     */
    override var grammaticalConfiguration: GrammaticalConfiguration? = null

    /**
     * Initialize the action.
     */
    init {
      require(this@Transition is SyntacticDependency)

      if (this.transition.isAllowed) {
        this@Transition as SyntacticDependency
        this.dependentId = this@Transition.dependentId
        this.governorId = this@Transition.governorId
      }
    }

    /**
     *
     */
    override fun perform(state: StateType) {

      require(this.dependentId != null) {
        "Required a not-null dependent."
      }

      if (this.governorId != null) {

        state.dependencyTree.setArc(
          dependent = this.dependentId!!,
          governor = this.governorId!!,
          grammaticalConfiguration = this.grammaticalConfiguration)

      } else if (this.grammaticalConfiguration != null) {

        state.dependencyTree.setGrammaticalConfiguration(
          dependent = this.dependentId!!,
          configuration = this.grammaticalConfiguration!!)
      }
    }

    /**
     * @return its string representation.
     */
    override fun toString(): String = "${this.grammaticalConfiguration?:"arc"}(${this.governorId} -> ${this.dependentId})"
  }

  /**
   * The 'track' of the [State] at the time this [Transition] has been created.
   * An action or a transition can be applied to the [refState] or a copy of it only if its 'track' has not changed.
   */
  val refStateTrack: Int = this.refState.track.get()

  /**
   * The Transition type, from which depends the building of the related [Action].
   */
  abstract val type: Type

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  abstract val priority: Int

  /**
   *  Whether the transition is allowed in the given parser state.
   */
  abstract val isAllowed: Boolean

  /**
   * @param id the id of the action
   *
   * @return a new [Action] tied to this transition
   */
  fun actionFactory(id: Int = -1, grammaticalConfiguration: GrammaticalConfiguration? = null): Action {

    val action: Action = this.buildAction(id)

    if (this is SyntacticDependency) {

      action as DependencyRelation // An arc Transition must be associated to a DependencyRelation

      action.grammaticalConfiguration = grammaticalConfiguration
    }

    return action
  }

  /**
   * Perform this [Transition] on the given [state].
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  protected abstract fun perform(state: StateType)

  /**
   * @param id the id of the action
   *
   * @return a new [Action] tied to this transition
   */
  private fun buildAction(id: Int = -1): Action = when (this.type) {
    Type.ARC_LEFT, Type.ARC_RIGHT, Type.ROOT -> this.buildArc(id)
    Type.NO_ARC -> this.buildNoArc(id)
    Type.RELOCATE -> this.buildRelocate(id)
    Type.SHIFT, Type.WAIT -> this.buildShift(id)
    Type.UNSHIFT -> this.buildUnshift(id)
  }

  /**
   * @param id the id of the action
   *
   * @return a new [Shift] tied to this transition
   */
  private fun buildShift(id: Int = -1) = this.Shift(id)

  /**
   * @param id the id of the action
   *
   * @return a new [Unshift] tied to this transition.
   */
  private fun buildUnshift(id: Int = -1) = this.Unshift(id)

  /**
   * @param id the id of the action
   *
   * @return a new [Relocate] tied to this transition
   */
  private fun buildRelocate(id: Int = -1) = this.Relocate(id)

  /**
   * @param id the id of the action
   *
   * @return a new [Arc] tied to this transition
   */
  private fun buildNoArc(id: Int = -1) = this.NoArc(id)

  /**
   * @param id the id of the action
   *
   * @return a new [Arc] tied to this transition
   */
  private fun buildArc(id: Int = -1) = this.Arc(id)
}
