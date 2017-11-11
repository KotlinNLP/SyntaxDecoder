/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.Token
import com.kotlinnlp.dependencytree.DependencyTree
import com.kotlinnlp.dependencytree.Deprel

/**
 * The Sentence.
 *
 * Very simple representation of a sentence where a token is just an id.
 *
 * @param tokens a list of consecutive id.
 * @param dependencyTree the dependency tree associated to the [tokens] (can be null).
 */
data class Sentence(val tokens: List<Int>, val dependencyTree: DependencyTree? = null){

  companion object {

    /**
     * Build a sentence from a CoNLL Sentence.
     *
     * @param sentence a ConLL Sentence.
     *
     * @return a new sentence.
     */
    fun fromCoNLL(sentence: com.kotlinnlp.conllio.Sentence) = Sentence(
      tokens = sentence.tokens.map { it.id - 1 },
      dependencyTree = if (sentence.hasAnnotatedHeads()) buildDependencyTree(sentence) else null
    )

    /**
     * Build a Dependency Tree from a [sentence].
     *
     * @param sentence a sentence.
     *
     * @return a new DependencyTree.
     */
    private fun buildDependencyTree(sentence: com.kotlinnlp.conllio.Sentence): DependencyTree {

      val dependencyTree = DependencyTree(size = sentence.tokens.size)

      sentence.tokens.forEach { token -> dependencyTree.addArc(token) }

      return dependencyTree
    }

    /**
     * Add the arc defined by the id, head and deprel of a given [token].
     *
     * @param token a token.
     */
    private fun DependencyTree.addArc(token: Token) {

      val id = token.id - 1
      val head = if (token.head!! == 0) null else token.head!! - 1

      val deprel = Deprel(
        label = token.deprel,
        direction = when {
          head == null -> Deprel.Position.ROOT
          head > id -> Deprel.Position.LEFT
          else -> Deprel.Position.RIGHT
        })

      if (head != null) {
        this.setArc(dependent = id, governor = head, deprel = deprel)
      } else {
        this.setDeprel(dependent = id, deprel = deprel)
      }
    }
  }
}
