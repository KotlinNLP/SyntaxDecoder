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
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * The FeaturesExtractor.
 */
interface FeaturesExtractor<
  StateType: State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType>,
  in StateViewType : StateView<StateType>,
  out FeaturesType : Features<*, *>> {

  /**
   * Extract features using the given [stateView] and [extendedState].
   *
   * @param stateView a view of the state
   * @param extendedState extended state context
   *
   * @return the extracted [Features]
   */
  fun extract(stateView: StateViewType,
              extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>): FeaturesType
}
