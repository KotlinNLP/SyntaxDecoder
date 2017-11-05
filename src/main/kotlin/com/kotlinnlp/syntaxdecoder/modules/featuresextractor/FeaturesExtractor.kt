/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.featuresextractor

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem

/**
 * The features extractor.
 */
abstract class FeaturesExtractor<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>,
  StructureType: FeaturesExtractorStructure<
    StructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>> {

  /**
   * Extract features using the given [featuresMemory] and set them into its 'features' property.
   *
   * @param featuresMemory the variable support featuresMemory in which to set the extracted features
   *
   * @return the extracted [Features]
   */
  fun setFeatures(
    featuresMemory: FeaturesExtractorMemory<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, StructureType>) {

    featuresMemory.features = this.extract(featuresMemory)
  }

  /**
   * @return a support structure for this [FeaturesExtractor]
   */
  abstract fun supportStructureFactory(): StructureType

  /**
   * Extract features using the given [featuresMemory].
   *
   * @param featuresMemory a featuresMemory containing a state view and an extended state.
   */
  abstract protected fun extract(
    featuresMemory: FeaturesExtractorMemory<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, StructureType>): FeaturesType
}
