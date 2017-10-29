/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.helpers.actionsscorer

import com.kotlinnlp.syntaxdecoder.Transition
import com.kotlinnlp.syntaxdecoder.state.DecodingContext
import com.kotlinnlp.syntaxdecoder.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.state.State
import com.kotlinnlp.syntaxdecoder.state.items.StateItem

/**
 * The [ActionsScorer] dynamic support structure, that is created for each transition.
 *
 * @property structure the support structure associated to this dynamic structure
 * @property actions the actions to score
 * @property extendedState the extended state to use for the scoring
 */
class ActionsScorerDynamicStructure<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StructureType : ActionsScorerStructure<StructureType, StateType, TransitionType, ContextType, ItemType>>
(
  val structure: StructureType,
  val actions: List<Transition<TransitionType, StateType>.Action>,
  val extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
)
