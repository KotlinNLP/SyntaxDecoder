/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.helpers.featuresextractor

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.utils.Updatable
import com.kotlinnlp.syntaxdecoder.helpers.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.utils.scheduling.BatchScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.EpochScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.ExampleScheduling
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView
import com.kotlinnlp.syntaxdecoder.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.items.StateItem

/**
 * The trainable [FeaturesExtractor].
 */
abstract class FeaturesExtractorTrainable<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>,
  StructureType: FeaturesExtractorStructure<
    StructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>>
  :
  FeaturesExtractor<StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, StructureType>(),
  ExampleScheduling,
  BatchScheduling,
  EpochScheduling,
  Updatable {

  /**
   * Backward errors through this [FeaturesExtractor], starting from the features of the given [featuresMemory].
   * Errors are required to be already set into the given features.
   *
   * @param featuresMemory the dynamic support featuresMemory that contains extracted features with their errors
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input items
   */
  abstract fun backward(
    featuresMemory: FeaturesExtractorMemory<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, StructureType>,
    propagateToInput: Boolean)
}
