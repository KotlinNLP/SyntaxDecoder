/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.BatchScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.EpochScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.ExampleScheduling
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem

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
   * Backward errors through this [FeaturesExtractor], starting from the features of the given [structure].
   * Errors are required to be already set into the given features.
   *
   * @param structure the dynamic support structure that contains extracted features with their errors
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input items
   */
  abstract fun backward(
    structure: FeaturesExtractorDynamicStructure<
      StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, StructureType>,
    propagateToInput: Boolean)
}
