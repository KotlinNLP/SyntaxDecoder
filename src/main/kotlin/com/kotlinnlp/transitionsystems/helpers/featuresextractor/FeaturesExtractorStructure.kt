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
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.state.stateview.StateView

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
   * @return a new dynamic structure associated to this static one
   */
  @Suppress("UNCHECKED_CAST")
  fun dynamicStructureFactory(
    stateView: StateViewType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ) =
    FeaturesExtractorDynamicStructure(
      structure = this as SelfType,
      stateView = stateView,
      extendedState = extendedState)
}
