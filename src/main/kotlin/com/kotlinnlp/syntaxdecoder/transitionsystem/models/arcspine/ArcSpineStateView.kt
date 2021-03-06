/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine

import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.transitions.*
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.StateTransitionView

/**
 * A view of a [ArcSpineState] related to the given [transition].
 *
 * @param state an ArcSpine state
 * @property transition an ArcSpine transition
 */
class ArcSpineStateView(
  state: ArcSpineState,
  transition: ArcSpineTransition
) : StateTransitionView<ArcSpineState, ArcSpineTransition>(
  state = state,
  transition = transition
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
  private var grandparent: Int? = null

  /**
   * Great Grandparent (can be null)
   */
  private var greatGrandparent: Int? = null

  /**
   * Initialize the virtual stack and buffer respect to the current 'stateView'.
   */
  init {
    when (this.transition) {
      is Shift -> this.extractShiftView()
      is ArcLeft -> this.extractArcLeftView()
      is ArcRight -> this.extractArcRightView()
      is Root -> this.extractRootView()
    }
  }

  /**
   * Fill the virtual stack and buffer with respect to the Shift transition.
   */
  private fun extractShiftView() {

    val stackLastIndex: Int =  this.state.stack.lastIndex

    this.stack[0] = this.state.stack.getTokenLeftSpine(stackLastIndex, 0)
    this.stack[1] = this.state.stack.getTokenLeftSpine(stackLastIndex - 1, 0)
    this.stack[2] = this.state.stack.getTokenLeftSpine(stackLastIndex - 2, 0)
    this.stack[3] = this.state.stack.getTokenLeftSpine(stackLastIndex - 3, 0)

    this.buffer[0] = this.state.buffer.getOrNull(0)
    this.buffer[1] = this.state.buffer.getOrNull(1)

    this.grandparent = null
    this.greatGrandparent = null
  }

  /**
   * Fill the virtual stack and buffer with respect to the ArcLeft transition.
   */
  private fun extractArcLeftView() {

    val stackLastIndex: Int = this.state.stack.lastIndex

    val k = (this.transition as ArcLeft).governorSpineIndex

    this.grandparent = null
    this.greatGrandparent = null

    this.stack[0] = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k)
    this.stack[1] = this.state.stack.getTokenLeftSpine(index = stackLastIndex - 1, k = 0)
    this.stack[2] = this.state.stack.getTokenLeftSpine(index = stackLastIndex - 2, k = 0)
    this.stack[3] = this.state.stack.getTokenLeftSpine(index = stackLastIndex - 3, k = 0)

    when (k) {
      0 -> {
        this.buffer[0] = this.state.buffer.getOrNull(0)
        this.buffer[1] = this.state.buffer.getOrNull(1)
      }

      1 -> {
        this.buffer[0] = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k - 1)
        this.buffer[1] = this.state.buffer.getOrNull(0)

        this.grandparent = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k - 1)
      }

      else -> {
        this.buffer[0] = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k - 1)
        this.buffer[1] = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k - 2)

        this.grandparent = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k - 1)
        this.greatGrandparent = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = k - 2)
      }
    }
  }

  /**
   * Fill the virtual stack and buffer with respect to the ArcRight transition.
   */
  private fun extractArcRightView() {

    val stackLastIndex: Int = this.state.stack.lastIndex

    val k = (this.transition as ArcRight).governorSpineIndex

    this.grandparent = null
    this.greatGrandparent = null

    this.stack[0] = this.state.stack.getTokenLeftSpine(index = stackLastIndex, k = 0)
    this.stack[1] = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k)

    when (k) {
      0 -> {
        this.stack[2] = this.state.stack.getTokenLeftSpine(index = stackLastIndex - 2, k = 0)
        this.stack[3] = this.state.stack.getTokenLeftSpine(index = stackLastIndex - 3, k = 0)
      }

      1 -> {
        this.stack[2] = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k - 1)
        this.stack[3] = this.state.stack.getTokenLeftSpine(index = stackLastIndex - 2, k = 0)

        this.grandparent = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k - 1)
      }

      else -> {
        this.stack[2] = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k - 1)
        this.stack[3] = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k - 2)

        this.grandparent = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k - 1)
        this.greatGrandparent = this.state.stack.getTokenRightSpine(index = stackLastIndex - 1, k = k - 2)
      }
    }

    this.buffer[0] = this.state.buffer.getOrNull(0)
    this.buffer[1] = this.state.buffer.getOrNull(1)
  }

  /**
   * Fill the virtual stack and buffer with respect to the Root transition.
   */
  private fun extractRootView() {
    this.stack[0] = this.state.stack.last().root
  }

  /**
   *
   */
  private fun MutableList<ArcSpineState.StackElement>.getTokenLeftSpine(index: Int, k: Int): Int? =
    if (index in 0 .. this.lastIndex && k in 0 .. this[index].leftSpine.lastIndex) this[index].leftSpine[k] else null

  /**
   *
   */
  private fun MutableList<ArcSpineState.StackElement>.getTokenRightSpine(index: Int, k: Int): Int? =
    if (index in 0 .. this.lastIndex && k in 0 .. this[index].rightSpine.lastIndex) this[index].rightSpine[k] else null
}
