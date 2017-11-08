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
import com.kotlinnlp.syntaxdecoder.utils.sortByScoreAndPriority

/**
 * The support structure created for each transition.
 * It contains data useful to extract features and score actions.
 *
 * @property structure the support structure associated to this memory
 * @property extendedState the extended state to use for the scoring
 * @property actions the actions to score
 */
open class TransitionSupportStructure<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  out StructureType : ScoringSupportStructure>
(
  val structure: StructureType,
  val extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
  val actions: List<Transition<TransitionType, StateType>.Action>
) {

  /**
   *
   */
  lateinit var features: FeaturesType

  /**
   * The [actions] sorted by descending score and then by transition priority.
   */
  val sortedActions: List<Transition<TransitionType, StateType>.Action> by lazy {
    this.actions.sortByScoreAndPriority()
  }
}
