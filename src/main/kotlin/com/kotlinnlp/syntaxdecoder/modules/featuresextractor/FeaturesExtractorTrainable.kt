/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.featuresextractor

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.utils.Updatable
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.utils.scheduling.BatchScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.EpochScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.ExampleScheduling
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.ScoringSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.TransitionSupportStructure

/**
 * The trainable [FeaturesExtractor].
 */
abstract class FeaturesExtractorTrainable<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  StructureType: ScoringSupportStructure<
    StructureType, StateType, TransitionType, ContextType, ItemType, FeaturesType>>
  :
  FeaturesExtractor<StateType, TransitionType, ContextType, ItemType, FeaturesType, StructureType>(),
  ExampleScheduling,
  BatchScheduling,
  EpochScheduling,
  Updatable {

  /**
   * Backward errors through this [FeaturesExtractor], starting from the features of the given [structure].
   * Errors are required to be already set into the given features.
   *
   * @param structure the transition support structure that contains extracted features with their errors
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input items
   */
  abstract fun backward(
    structure: TransitionSupportStructure<
      StateType, TransitionType, ContextType, ItemType, FeaturesType, StructureType>,
    propagateToInput: Boolean)
}
