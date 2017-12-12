/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.supportstructures

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.utils.sortByScoreAndPriority

/**
 * A support structure used to score actions and extract features.
 * It is created for each decoding step (action applied).
 *
 * @property structure the support structure associated to this memory
 * @property extendedState the extended state to use for the scoring
 * @property actions the actions to score
 */
open class ScoringSupportStructure<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  out StructureType : ScoringGlobalSupportStructure>
(
  val structure: StructureType,
  val extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
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
