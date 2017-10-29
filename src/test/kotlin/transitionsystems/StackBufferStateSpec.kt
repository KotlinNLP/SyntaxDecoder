/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import transitionsystems.Utils
import com.kotlinnlp.syntaxdecoder.state.templates.StackBufferState
import kotlin.test.assertEquals

object StackBufferStateSpec: Spek({

  describe("a StackBufferState") {

    context("initialization with empty Sentence") {

      val sentence = Utils.buildEmptySentence()
      val state = StackBufferState(sentence)

      it("should be the finale state") {
        assertEquals(true, state.isTerminal)
      }

      on("copy") {
        val clonedState = state.copy()

        it("should contain empty stack and buffer") {
          assertEquals(true, clonedState.stack.isEmpty())
          assertEquals(true, clonedState.buffer.isEmpty())
        }

        it("should contain the same dependencyTree (empty)") {
          assertEquals(true, clonedState.dependencyTree.match(state.dependencyTree))
        }
      }
    }

    context("initialization with Sentence of 5 Tokens") {

      val sentence = Utils.buildSentence5()
      val state = StackBufferState(sentence)

      it("should not be the finale state") {
        assertEquals(false, state.isTerminal)
      }

      on("copy") {
        val clonedState = state.copy()

        it("should contain the same stack and buffer") {
          assertEquals(true, clonedState.stack == state.stack)
          assertEquals(true, clonedState.buffer == state.buffer)
        }

        it("should contain the same dependencyTree") {
          assertEquals(true, clonedState.dependencyTree.match(state.dependencyTree))
        }
      }
    }

    context("initialization with Sentence of 5 Tokens which has a DependencyTree") {

      val sentence = Utils.buildSentence5()

      val state = StackBufferState(sentence)

      on("copy") {
        val clonedState = state.copy()

        it("should contain the same dependencyTree") {
          assertEquals(true, clonedState.dependencyTree.match(state.dependencyTree))
        }
      }
    }
  }
})
