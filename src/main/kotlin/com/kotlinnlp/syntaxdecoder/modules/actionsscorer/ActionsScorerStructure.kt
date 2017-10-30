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

/**
 * The [ActionsScorer] support structure.
 */
interface ActionsScorerStructure<
  SelfType: ActionsScorerStructure<SelfType, StateType, TransitionType, ContextType, ItemType>,
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>> {

  /**
   * @param actions the actions to score
   * @param extendedState the extended state to use for the scoring
   *
   * @return a new memory associated to this support structure
   */
  @Suppress("UNCHECKED_CAST")
  fun buildMemoryOf(
    actions: List<Transition<TransitionType, StateType>.Action>,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>
  ) =
    ActionsScorerMemory(
      structure = this as SelfType,
      actions = actions,
      extendedState = extendedState)
}
