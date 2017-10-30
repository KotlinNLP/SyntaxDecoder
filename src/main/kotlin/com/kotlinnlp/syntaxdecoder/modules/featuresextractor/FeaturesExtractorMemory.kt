/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.featuresextractor

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView

/**
 * The [FeaturesExtractor] memory, that is created for each transition.
 *
 * @property structure the support structure associated to this dynamic structure
 * @property stateView the state view used as adding context to extract features
 * @property extendedState the extended state context used to extract features
 */
class FeaturesExtractorMemory<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>,
  StructureType : FeaturesExtractorStructure<
    StructureType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>>
(
  val structure: StructureType,
  val stateView: StateViewType,
  val extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
) {
  lateinit var features: FeaturesType
}
