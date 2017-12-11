/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.CoNLLReader
import com.kotlinnlp.progressindicator.ProgressIndicatorBar
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine.ArcEagerSpine
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arceagerspine.ArcEagerSpineOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybrid
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybridNP
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybridNPOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.archybrid.ArcHybridOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpine
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineNonDetOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcspine.ArcSpineOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.ArcStandard
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.ArcStandardNonDetOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcstandard.ArcStandardOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwift
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcswift.ArcSwiftOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcdistance.ArcDistance
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.arcdistance.ArcDistanceOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.Covington
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.CovingtonOracle
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.NLCovington
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
      oracleFactory = ArcStandardOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_STANDARD_NON_DET -> TransitionSystemCoverage(
      transitionSystem = ArcStandard(),
      oracleFactory = ArcStandardNonDetOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_HYBRID -> TransitionSystemCoverage(
      transitionSystem = ArcHybrid(),
      oracleFactory = ArcHybridOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_HYBRID_NON_PROJECTIVE -> TransitionSystemCoverage(
      transitionSystem = ArcHybridNP(),
      oracleFactory = ArcHybridNPOracle.Factory,
      errorExploring = false,
      verbose = false)

    TransitionSystemType.ARC_SWIFT -> TransitionSystemCoverage(
      transitionSystem = ArcSwift(),
      oracleFactory = ArcSwiftOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_SPINE -> TransitionSystemCoverage(
      transitionSystem = ArcSpine(),
      oracleFactory = ArcSpineOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_SPINE_NON_DET -> TransitionSystemCoverage(
      transitionSystem = ArcSpine(),
      oracleFactory = ArcSpineNonDetOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_EAGER_SPINE -> TransitionSystemCoverage(
      transitionSystem = ArcEagerSpine(),
      oracleFactory = ArcEagerSpineOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_EAGER_SPINE_EXPLORE -> TransitionSystemCoverage(
      transitionSystem = ArcEagerSpine(),
      oracleFactory = ArcEagerSpineOracle.Factory,
      errorExploring = true)

    TransitionSystemType.EASY_FIRST -> TransitionSystemCoverage(
      transitionSystem = EasyFirst(),
      oracleFactory = EasyFirstOracle.Factory,
      errorExploring = false)

    TransitionSystemType.ARC_RELOCATE -> TODO("not-implemented")

    TransitionSystemType.COVINGTON ->  TransitionSystemCoverage(
      transitionSystem = Covington(),
      oracleFactory = CovingtonOracle.Factory,
      errorExploring = false,
      verbose = false)

    TransitionSystemType.NON_LOCAL_COVINGTON ->  TransitionSystemCoverage(
      transitionSystem = NLCovington(),
      oracleFactory = CovingtonOracle.Factory,
      errorExploring = false,
      verbose = false)

    TransitionSystemType.ATTARDI ->  TransitionSystemCoverage(
      transitionSystem = ArcDistance(),
      oracleFactory = ArcDistanceOracle.Factory,
      errorExploring = false,
      verbose = false)
  }

  println("Start 'Coverage Stats'")

  val sentences = ArrayList<Sentence>()

  sentences.loadFromTreeBank(args[0])

  val progress = ProgressIndicatorBar(sentences.size)

  sentences.forEach {
    progress.tick()
    transitionSystemCoverage(TransitionSystemType.valueOf(args[1])).run(it)
  }
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
