/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.featuresextractor

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.features.Features
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.state.stateview.StateView

/**
 * The simplest support structure of a features extractor.
 */
class SimpleFeaturesExtractorStructure<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>>
  :
  FeaturesExtractorStructure<
    SimpleFeaturesExtractorStructure<StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>,
    StateType,
    TransitionType,
    ContextType,
    ItemType,
    StateViewType,
    FeaturesType>()
