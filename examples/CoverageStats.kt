/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.CoNLLReader
import com.kotlinnlp.syntaxdecoder.models.arceagerspine.ArcEagerSpine
import com.kotlinnlp.syntaxdecoder.models.arceagerspine.ArcEagerSpineOracle
import com.kotlinnlp.syntaxdecoder.models.archybrid.ArcHybrid
import com.kotlinnlp.syntaxdecoder.models.archybrid.ArcHybridOracle
import com.kotlinnlp.syntaxdecoder.models.arcspine.ArcSpine
import com.kotlinnlp.syntaxdecoder.models.arcspine.ArcSpineNonDetOracle
import com.kotlinnlp.syntaxdecoder.models.arcspine.ArcSpineOracle
import com.kotlinnlp.syntaxdecoder.models.arcstandard.ArcStandard
import com.kotlinnlp.syntaxdecoder.models.arcstandard.ArcStandardNonDetOracle
import com.kotlinnlp.syntaxdecoder.models.arcstandard.ArcStandardOracle
import com.kotlinnlp.syntaxdecoder.models.arcswift.ArcSwift
import com.kotlinnlp.syntaxdecoder.models.arcswift.ArcSwiftOracle
import com.kotlinnlp.syntaxdecoder.models.easyfirst.EasyFirst
import com.kotlinnlp.syntaxdecoder.models.easyfirst.EasyFirstOracle

/**
 * Test the coverage a [TransitionSystem] for the language with the tree-bank given as first argument.
 * The second argument is the [TransitionSystemType].
 */

fun main(args: Array<String>){

  fun transitionSystemCoverage(transitionSystemType: TransitionSystemType) = when (transitionSystemType) {

    TransitionSystemType.ARC_STANDARD -> TransitionSystemCoverage(
      transitionSystem = ArcStandard(),
      oracle = ArcStandardOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_STANDARD_NON_DET -> TransitionSystemCoverage(
      transitionSystem = ArcStandard(),
      oracle = ArcStandardNonDetOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_HYBRID -> TransitionSystemCoverage(
      transitionSystem = ArcHybrid(),
      oracle = ArcHybridOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_SWIFT -> TransitionSystemCoverage(
      transitionSystem = ArcSwift(),
      oracle = ArcSwiftOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_SPINE -> TransitionSystemCoverage(
      transitionSystem = ArcSpine(),
      oracle = ArcSpineOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_SPINE_NON_DET -> TransitionSystemCoverage(
      transitionSystem = ArcSpine(),
      oracle = ArcSpineNonDetOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_EAGER_SPINE -> TransitionSystemCoverage(
      transitionSystem = ArcEagerSpine(),
      oracle = ArcEagerSpineOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_EAGER_SPINE_EXPLORE -> TransitionSystemCoverage(
      transitionSystem = ArcEagerSpine(),
      oracle = ArcEagerSpineOracle(),
      errorExploring = true)

    TransitionSystemType.EASY_FIRST -> TransitionSystemCoverage(
      transitionSystem = EasyFirst(),
      oracle = EasyFirstOracle(),
      errorExploring = false)

    TransitionSystemType.ARC_RELOCATE -> TODO("not-implemented")

    TransitionSystemType.COVINGTON -> TODO("not-implemented")
  }

  println("Start 'Coverage Stats'")

  val sentences = ArrayList<Sentence>()

  sentences.loadFromTreeBank(args[0])

  transitionSystemCoverage(TransitionSystemType.valueOf(args[1])).testCoverage(sentences)
}

/**
 * Populate an array of sentences from a tree-bank.
 *
 * @param filePath a tree-bank file path.
 */
fun ArrayList<Sentence>.loadFromTreeBank(filePath: String) =
  CoNLLReader.fromFile(filePath).forEach { it ->
    if (it.hasAnnotatedHeads()) it.assertValidCoNLLTree()
    this.add(Sentence.fromCoNLL(it))
  }
