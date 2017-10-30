/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.actionsscorer

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.items.StateItem
import com.kotlinnlp.syntaxdecoder.utils.sortByScoreAndPriority

/**
 * The [ActionsScorer] memory, that is created for each transition.
 *
 * @property structure the support structure associated to this memory
 * @property actions the actions to score
 * @property extendedState the extended state to use for the scoring
 */
class ActionsScorerMemory<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  StructureType : ActionsScorerStructure<StructureType, StateType, TransitionType, ContextType, ItemType>>
(
  val structure: StructureType,
  val actions: List<Transition<TransitionType, StateType>.Action>,
  val extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
) {

  /**
   * The [actions] sorted by score and then by transition priority.
   */
  val sortedActions: List<Transition<TransitionType, StateType>.Action> by lazy {
    this.actions.sortByScoreAndPriority()
  }
}
