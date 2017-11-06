/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.featuresextractor

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.ScoringSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.TransitionSupportStructure

/**
 * The features extractor.
 */
abstract class FeaturesExtractor<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  StructureType: ScoringSupportStructure<
    StructureType, StateType, TransitionType, ContextType, ItemType, FeaturesType>> {

  /**
   * Set the features property in the given [supportStructure].
   *
   * @param supportStructure the transition support structure in which to set the extracted features
   */
  fun setFeatures(
    supportStructure: TransitionSupportStructure<
      StateType, TransitionType, ContextType, ItemType, FeaturesType, StructureType>) {

    supportStructure.features = this.extract(supportStructure)
  }

  /**
   * Extract features using the given [supportStructure].
   *
   * @param supportStructure a support structure containing a state view and an extended state.
   *
   * @return the extracted [Features]
   */
  abstract protected fun extract(
    supportStructure: TransitionSupportStructure<
      StateType, TransitionType, ContextType, ItemType, FeaturesType, StructureType>): FeaturesType
}
