/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.utils

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features

/**
 * A decoding context used as container of information though a decoding step.
 * It is created for each decoding step (action applied).
 *
 * @property extendedState the extended state to use for the scoring
 * @property actions the actions to score
 */
data class DecodingContext<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>>
(
  val extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
  val actions: List<Transition<TransitionType, StateType>.Action>
) {

  /**
   * The features extracted in the current decoding step, used to score actions.
   */
  lateinit var features: FeaturesType

  /**
   * The [actions] sorted by descending score and then by transition priority.
   */
  val sortedActions: List<Transition<TransitionType, StateType>.Action> by lazy {
    this.actions.sortByScoreAndPriority()
  }
}
