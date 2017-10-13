/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.ExtendedState
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem

/**
 * The ActionsScorer that implements [Trainable].
 *
 * @property featuresExtractor a [FeaturesExtractor]
 */
abstract class ActionsScorerTrainable<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>,
  ItemType : StateItem<ItemType, *, *>,
  ExtendedStateType : ExtendedState<ExtendedStateType, StateType, ItemType, ContextType>>
(
  featuresExtractor: FeaturesExtractor<StateViewType, ContextType, FeaturesType>
) :
  ActionsScorer<
    StateType,
    TransitionType,
    StateViewType,
    ContextType,
    FeaturesType,
    ItemType,
    ExtendedStateType>(featuresExtractor),
  Trainable {

  /**
   * Backward errors through this [ActionsScorer], starting from the output actions, eventually accumulating them into
   * proper structures.
   * Errors are required to be already set into the output proper.
   *
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input
   */
  override fun backward(propagateToInput: Boolean) {

    this.propagateErrors()

    if (this.featuresExtractor is FeaturesExtractorTrainable) {
      this.featuresExtractor.backward(propagateToInput = propagateToInput)
    }
  }

  /**
   * Propagate errors through this [ActionsScorer], setting its features errors.
   */
  abstract fun propagateErrors()
}
