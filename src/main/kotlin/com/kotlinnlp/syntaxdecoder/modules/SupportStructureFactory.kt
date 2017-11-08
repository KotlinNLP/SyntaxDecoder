/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.ExtendedState

/**
 * The factory of a [ScoringSupportStructure] and its related [TransitionSupportStructure].
 */
interface SupportStructureFactory<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  StructureType : ScoringSupportStructure>
{

  /**
   * Build a new [ScoringSupportStructure].
   *
   * @return a new scoring support structure
   */
  fun scoringStructure(): StructureType

  /**
   * Build a new [TransitionSupportStructure] associated to the given [scoringSupportStructure].
   *
   * @param scoringSupportStructure a scoring support structure
   * @param extendedState the extended state
   * @param actions the actions to score
   *
   * @return a new transition support structure associated to the given [scoringSupportStructure]
   */
  fun transitionStructure(
    scoringSupportStructure: StructureType,
    extendedState: ExtendedState<StateType, TransitionType, ItemType, ContextType>,
    actions: List<Transition<TransitionType, StateType>.Action>
  ): TransitionSupportStructure<StateType, TransitionType, ContextType, ItemType, FeaturesType, StructureType>
    = TransitionSupportStructure(
    structure = scoringSupportStructure,
    extendedState = extendedState,
    actions = actions)
}
