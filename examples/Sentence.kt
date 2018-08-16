/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.Token as CoNLLToken
import com.kotlinnlp.conllio.Sentence as CoNLLSentence
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
data class Sentence(val tokens: List<Int>, val dependencyTree: DependencyTree? = null) {

  companion object {

    /**
     * Build a sentence from a CoNLL Sentence.
     *
     * @param sentence a ConLL Sentence
     *
     * @return a new sentence
     */
    fun fromCoNLL(sentence: CoNLLSentence) = Sentence(
      tokens = sentence.tokens.map { it.id },
      dependencyTree = if (sentence.hasAnnotatedHeads()) buildDependencyTree(sentence) else null
    )

    /**
     * Build a Dependency Tree from a [CoNLLSentence].
     *
     * @param sentence a sentence
     *
     * @return a new dependency tree
     */
    private fun buildDependencyTree(sentence: CoNLLSentence): DependencyTree {

      val dependencyTree = DependencyTree(sentence.tokens.map { it.id })

      sentence.tokens.forEach { token -> dependencyTree.addArc(token) }

      return dependencyTree
    }

    /**
     * Add the arc defined by the id, head and deprel of a given [CoNLLToken].
     *
     * @param token a CoNLL token
     */
    private fun DependencyTree.addArc(token: CoNLLToken) {

      val head: Int = token.head!!

      val deprel = Deprel(
        label = token.deprel,
        direction = when {
          head == 0 -> Deprel.Position.ROOT
          head > token.id -> Deprel.Position.LEFT
          else -> Deprel.Position.RIGHT
        })

      if (head > 0)
        this.setArc(dependent = token.id, governor = head, deprel = deprel)
      else
        this.setDeprel(dependent = token.id, deprel = deprel)
    }
  }
}
