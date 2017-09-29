/*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import transitionsystems.Utils
import com.kotlinnlp.transitionsystems.StackBufferState
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


object StackBufferStateSpec: Spek({

  describe("a StackBufferState") {

    context("initialization with empty Sentence") {

      val sentence = Utils.buildEmptySentence()
      val state = StackBufferState(sentence, goldDependencyTree = null)

      it("should be the finale state") {
        assertEquals(true, state.isFinal)
      }

      on("clone") {
        val clonedState = state.clone()

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
      val state = StackBufferState(sentence, goldDependencyTree = null)

      it("should not be the finale state") {
        assertEquals(false, state.isFinal)
      }

      on("clone") {
        val clonedState = state.clone()

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

      val state = StackBufferState(sentence, goldDependencyTree = Utils.buildDependencyTree())

      it("should contain a not-null missingDependents") {
        assertNotNull(state.missingDependents)
      }

      on("clone") {
        val clonedState = state.clone()

        it("should contain the same dependencyTree") {
          assertEquals(true, clonedState.dependencyTree.match(state.dependencyTree))
        }
      }
    }
  }
})

  */