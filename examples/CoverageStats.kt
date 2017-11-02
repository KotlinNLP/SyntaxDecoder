/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.CoNLLReader
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine.ArcEagerSpine
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine.ArcEagerSpineOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybrid
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybridOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpine
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineNonDetOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.ArcStandard
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.ArcStandardNonDetOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.ArcStandardOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwift
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwiftOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.Covington
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.EasyFirst
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.easyfirst.EasyFirstOracle

/**
 * Test the coverage a Transition System for the language with the given tree-bank.
 *
 * Command line arguments:
 *   1. The filename of the test tree-bank.
 *   2. The [TransitionSystemType].
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

    TransitionSystemType.COVINGTON ->  TransitionSystemCoverage(
      transitionSystem = Covington(),
      oracle = CovingtonOracle(),
      errorExploring = false,
      verbose = true)
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
