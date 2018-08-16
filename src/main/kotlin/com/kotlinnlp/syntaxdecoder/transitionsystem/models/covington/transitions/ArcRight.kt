/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions

import com.kotlinnlp.syntaxdecoder.syntax.SyntacticDependency
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonState
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonTransition
import com.kotlinnlp.syntaxdecoder.utils.removeFrom

/**
 * The ArcRight transition.
 *
 * @property refState the [State] on which this transition operates
 * @property governorStack1Index the position in the stack 1 of the governor element
 * @property id the transition id
 */
class ArcRight(
  refState: CovingtonState,
  val governorStack1Index: Int,
  id: Int
) : CovingtonTransition(refState, id), SyntacticDependency {

  /**
   * The Transition type, from which depends the building of the related Action.
   */
  override val type: Type = Type.ARC_RIGHT

  /**
   * The priority of the transition in case of spurious-ambiguities.
   */
  override val priority: Int = 4

  /**
   * The governor id.
   */
  override val governorId: Int get() = this.refState.stack1[this.refState.stack1.size - this.governorStack1Index]

  /**
   * The dependent id.
   */
  override val dependentId: Int get() = this.refState.buffer.first()

  /**
   * Returns True if the action is allowed in the given parser state.
   */
  override val isAllowed: Boolean get() = this.refState.buffer.isNotEmpty()
    && this.refState.stack1.size >= this.governorStack1Index
    && this.refState.dependencyTree.getHead(this.dependentId) == null
    && !this.refState.dependencyTree.introduceCycle(dependent = this.dependentId, governor = this.governorId)

  /**
   * Perform this [Transition] on the given [state].
   *
   * It requires that the transition [isAllowed] on the given [state], however it is guaranteed that the [state] is
   * compatible with this [Transition] as it can only be the [refState] or a copy of it.
   *
   * @param state a State
   */
  override fun perform(state: CovingtonState) {

    val fromIndex = this.refState.stack1.size - this.governorStack1Index

    state.stack2.addAll(0, state.stack1.slice(fromIndex .. state.stack1.lastIndex))

    state.stack1.removeFrom(fromIndex)
  }

  /**
   * @return the string representation of this transition.
   */
  override fun toString(): String = "arc-right(${this.governorStack1Index})"
}
