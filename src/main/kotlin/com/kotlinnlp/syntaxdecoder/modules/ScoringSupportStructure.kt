/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features

/**
 * The support structure created for each scoring.
 * It contains data useful to extract features and score actions.
 */
interface ScoringSupportStructure<
  SelfType: ScoringSupportStructure<SelfType, StateType, TransitionType, ContextType, ItemType, FeaturesType>,
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>> {

  /**
   * @param extendedState the extended state
   * @param actions the actions to score
   *
   * @return a new transition support structure associated to this one
   */
  @Suppress("UNCHECKED_CAST")
  fun buildTransitionStructure(
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
    actions: List<Transition<TransitionType, StateType>.Action>
  )
    = TransitionSupportStructure(structure = this as SelfType, extendedState = extendedState, actions = actions)
}
