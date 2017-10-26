/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * The [FeaturesExtractor] that implements [Trainable].
 */
interface FeaturesExtractorTrainable<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType>,
  in StateViewType : StateView<StateType>,
  out FeaturesType : Features<*, *>>
  :
  FeaturesExtractor<StateType, TransitionType, ItemType, ContextType, StateViewType, FeaturesType>,
  Trainable
