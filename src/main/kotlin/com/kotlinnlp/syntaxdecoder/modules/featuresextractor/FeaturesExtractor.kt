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
import com.kotlinnlp.syntaxdecoder.modules.supportstructure.DecodingSupportStructure
import com.kotlinnlp.syntaxdecoder.utils.DecodingContext

/**
 * The features extractor.
 */
abstract class FeaturesExtractor<
  StateType: State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  in SupportStructureType : DecodingSupportStructure> {

  /**
   * Set the features property in the given [decodingContext] using the given [supportStructure].
   *
   * @param decodingContext the decoding context in which to set the extracted features
   * @param supportStructure the decoding support structure
   */
  fun setFeatures(
    decodingContext: DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>,
    supportStructure: SupportStructureType
  ) {
    decodingContext.features = this.extract(decodingContext = decodingContext, supportStructure = supportStructure)
  }

  /**
   * Extract features using the given [decodingContext] amd [supportStructure].
   *
   * @param decodingContext the decoding context
   * @param supportStructure the decoding support structure
   *
   * @return the extracted features
   */
  abstract protected fun extract(
    decodingContext: DecodingContext<StateType, TransitionType, InputContextType, ItemType, FeaturesType>,
    supportStructure: SupportStructureType
  ): FeaturesType
}
