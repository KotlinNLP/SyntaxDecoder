/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine.transitions.*
import com.kotlinnlp.syntaxdecoder.utils.getItemOrNull

/**
 * The ArcEagerSpineVirtualState is a higher level of abstraction of a [ArcEagerSpineState] providing a uniform view
 * of the state in relation with a specific transition.
 *
 * @param state an ArcEagerSpine state
 * @property transition an ArcEagerSpine transition
 */
class ArcEagerSpineVirtualState(
  private val state: ArcEagerSpineState,
  val transition: Transition<ArcEagerSpineTransition, ArcEagerSpineState>
) {

  /**
   * The virtual stack.
   */
  val stack = arrayOfNulls<Int>(size = 4)

  /**
   * The virtual buffer.
   */
  val buffer = arrayOfNulls<Int>(size = 2)

  /**
   * Grandparent (can be null)
   */
  var grandparent: Int? = null
    private set

  /**
   * Great Grandparent (can be null)
   */
  var greatGrandparent: Int? = null
    private set

  /**
   * Initialize the virtual stack and buffer respect to the current 'stateView'.
   */
  init {
    when (this.transition) {
      is Shift -> this.extractShiftContext()
      is ArcLeft -> this.extractArcLeftContext()
      is ArcRight -> this.extractArcRightContext()
      is Root -> this.extractRootContext()
    }
  }

  /**
   * Fill the virtual stack and buffer with respect to the Shift transition.
   */
  private fun extractShiftContext() {

    this.stack[0] = this.state.stack.getItemOrNull(-1)?.root
    this.stack[1] = this.state.stack.getItemOrNull(-2)?.root
    this.stack[2] = this.state.stack.getItemOrNull(-3)?.root
    this.buffer[0] = this.state.buffer.getOrNull(0)
    this.buffer[1] = this.state.buffer.getOrNull(1)

    this.grandparent = null
    this.greatGrandparent = null
  }

  /**
   * Fill the virtual stack and buffer with respect to the ArcLeft transition.
   */
  private fun extractArcLeftContext() {

    this.stack[0] = this.state.stack.getItemOrNull(-1)?.root
    this.stack[1] = this.state.stack.getItemOrNull(-2)?.root
    this.stack[2] = this.state.stack.getItemOrNull(-3)?.root
    this.buffer[0] = this.state.buffer.getOrNull(0)
    this.buffer[1] = this.state.buffer.getOrNull(1)

    this.grandparent = null
    this.greatGrandparent = null
  }

  /**
   * Fill the virtual stack and buffer with respect to the ArcRight transition.
   */
  private fun extractArcRightContext() {

    val stackLastIndex: Int = this.state.stack.lastIndex
    val k = (this.transition as ArcRight).governorSpineIndex

    this.grandparent = null
    this.greatGrandparent = null

    when (k) {
      0 -> {
        this.stack[0] = this.state.stack.getItemOrNull(-1)?.root
        this.stack[1] = this.state.stack.getItemOrNull(-2)?.root
        this.stack[2] = this.state.stack.getItemOrNull(-3)?.root
      }

      1 -> {
        this.stack[0] = this.state.stack.getTokenRightSpine(index = stackLastIndex, k = 1)
        this.stack[1] = this.state.stack.getTokenRightSpine(index = stackLastIndex, k = 0)
        this.stack[2] = this.state.stack.getItemOrNull(-2)?.root

        this.grandparent = this.stack[1]
      }

      else -> {
        this.stack[0] = this.state.stack.getTokenRightSpine(index = stackLastIndex, k = k)
        this.stack[1] = this.state.stack.getTokenRightSpine(index = stackLastIndex, k = k - 1)
        this.stack[2] = this.state.stack.getTokenRightSpine(index = stackLastIndex, k = k - 2)

        this.grandparent = this.stack[1]
        this.greatGrandparent = this.stack[2]
      }
    }

    this.buffer[0] = this.state.buffer.getOrNull(0)
    this.buffer[1] = this.state.buffer.getOrNull(1)
  }

  /**
   * Fill the virtual stack and buffer with respect to the Root transition.
   */
  private fun extractRootContext() {
    this.stack[0] = this.state.stack.last().root
  }

  /**
   *
   */
  private fun ArrayList<ArcEagerSpineState.StackElement>.getTokenRightSpine(index: Int, k: Int): Int? {
    return if (index in 0 .. this.lastIndex && k in 0 .. this[index].lastIndex){
      this[index][k]
    } else {
      null
    }
  }
}