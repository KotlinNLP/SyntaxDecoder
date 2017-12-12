/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.featuresextractor

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringGlobalSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringSupportStructure

/**
 * The features extractor.
 */
abstract class FeaturesExtractor<
  StateType: State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  out ScoringGlobalStructureType : ScoringGlobalSupportStructure,
  in ScoringStructureType : ScoringSupportStructure<StateType, TransitionType, InputContextType, ItemType,
    FeaturesType, ScoringGlobalStructureType>> {

  /**
   * Set the features property in the given [supportStructure].
   *
   * @param supportStructure the scoring support structure in which to set the extracted features
   */
  fun setFeatures(supportStructure: ScoringStructureType) {
    supportStructure.features = this.extract(supportStructure)
  }

  /**
   * Extract features using the given [structure].
   *
   * @param structure the scoring support structure
   *
   * @return the extracted [Features]
   */
  abstract protected fun extract(structure: ScoringStructureType): FeaturesType
}
