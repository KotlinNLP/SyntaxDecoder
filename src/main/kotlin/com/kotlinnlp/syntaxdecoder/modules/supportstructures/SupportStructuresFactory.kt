/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.supportstructures

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.InputContext
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState

/**
 * The factory of a [ScoringGlobalSupportStructure] and its related [ScoringSupportStructure].
 */
interface SupportStructuresFactory<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  InputContextType : InputContext<InputContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  ScoringGlobalStructureType : ScoringGlobalSupportStructure,
  out ScoringStructureType : ScoringSupportStructure<StateType, TransitionType, InputContextType, ItemType,
    FeaturesType, ScoringGlobalStructureType>>
{

  /**
   * Build a new [ScoringGlobalSupportStructure].
   *
   * @return a new scoring global support structure
   */
  fun globalStructure(): ScoringGlobalStructureType

  /**
   * Build a new [ScoringSupportStructure] associated to the given [scoringGlobalSupportStructure].
   *
   * @param scoringGlobalSupportStructure a scoring global support structure
   * @param extendedState the extended state
   * @param actions the actions to score
   *
   * @return a new scoring support structure associated to the given [scoringGlobalSupportStructure]
   */
  fun localStructure(scoringGlobalSupportStructure: ScoringGlobalStructureType,
                     extendedState: ExtendedState<StateType, TransitionType, ItemType, InputContextType>,
                     actions: List<Transition<TransitionType, StateType>.Action>): ScoringStructureType
}
