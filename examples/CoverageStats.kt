/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

import com.kotlinnlp.conllio.CoNLLReader
import com.kotlinnlp.transitionsystems.arceagerspine.ArcEagerSpine
import com.kotlinnlp.transitionsystems.arceagerspine.ArcEagerSpineOracle
import com.kotlinnlp.transitionsystems.archybrid.ArcHybrid
import com.kotlinnlp.transitionsystems.archybrid.ArcHybridOracle
import com.kotlinnlp.transitionsystems.arcspine.ArcSpine
import com.kotlinnlp.transitionsystems.arcspine.ArcSpineNonDetOracle
import com.kotlinnlp.transitionsystems.arcspine.ArcSpineOracle
import com.kotlinnlp.transitionsystems.arcstandard.ArcStandard
import com.kotlinnlp.transitionsystems.arcstandard.ArcStandardNonDetOracle
import com.kotlinnlp.transitionsystems.arcstandard.ArcStandardOracle
import com.kotlinnlp.transitionsystems.arcswift.ArcSwift
import com.kotlinnlp.transitionsystems.arcswift.ArcSwiftOracle
import com.kotlinnlp.transitionsystems.easyfirst.EasyFirst
import com.kotlinnlp.transitionsystems.easyfirst.EasyFirstOracle

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
