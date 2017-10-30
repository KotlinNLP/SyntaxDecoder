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
 *
 */
open class FeaturesExtractorStructure<
  SelfType: FeaturesExtractorStructure<
    SelfType, StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType>,
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StateViewType : StateView<StateType>,
  FeaturesType : Features<*, *>> {

  /**
   * @param stateView the state view used as adding context to extract features
   * @param extendedState the extended state context used to extract features
   *
   * @return a new memory associated to this support structure
   */
  @Suppress("UNCHECKED_CAST")
  fun buildMemoryOf(
    stateView: StateViewType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ) =
    FeaturesExtractorMemory(
      structure = this as SelfType,
      stateView = stateView,
      extendedState = extendedState)
}
