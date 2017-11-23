/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.syntaxdecoder.utils.groupBySize
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

object ExtensionsSpec : Spek({

  describe("extension functions") {

    context("List<T>.groupBySize()") {

      on("List of 10 elements with groupSize = 4") {

        val l = listOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        val groups = l.groupBySize(4)

        it("should return 3 groups") {
          assertEquals(3, groups.size)
        }

        it("should return the first group with size 4") {
          assertEquals(4, groups[0].size)
        }

        it("should return the second group with size 3") {
          assertEquals(3, groups[1].size)
        }

        it("should return the third group with size 3") {
          assertEquals(3, groups[2].size)
        }
      }

      on("List of 7 elements with groupSize = 3") {

        val l = listOf(11, 12, 13, 14, 15, 16, 17)
        val groups = l.groupBySize(3)

        it("should return 3 groups") {
          assertEquals(3, groups.size)
        }

        it("should return the first group with size 3") {
          assertEquals(3, groups[0].size)
        }

        it("should return the second group with size 2") {
          assertEquals(2, groups[1].size)
        }

        it("should return the third group with size 2") {
          assertEquals(2, groups[2].size)
        }
      }

      on("List of 4 elements with groupSize = 1") {

        val l = listOf(11, 12, 13, 14)
        val groups = l.groupBySize(1)

        it("should return 4 groups") {
          assertEquals(4, groups.size)
        }

        it("should return the first group with size 1") {
          assertEquals(1, groups[0].size)
        }

        it("should return the second group with size 1") {
          assertEquals(1, groups[1].size)
        }

        it("should return the third group with size 1") {
          assertEquals(1, groups[2].size)
        }

        it("should return the fourth group with size 1") {
          assertEquals(1, groups[3].size)
        }
      }

      on("List of 4 elements with groupSize = 3") {

        val l = listOf(11, 12, 13, 14)
        val groups = l.groupBySize(3)

        it("should return 2 groups") {
          assertEquals(2, groups.size)
        }

        it("should return the first group with size 2") {
          assertEquals(2, groups[0].size)
        }

        it("should return the second group with size 2") {
          assertEquals(2, groups[1].size)
        }
      }
    }
  }
})
