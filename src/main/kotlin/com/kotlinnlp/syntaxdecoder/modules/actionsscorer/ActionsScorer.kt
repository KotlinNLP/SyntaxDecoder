/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.actionsscorer

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.TransitionSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.ScoringSupportStructure

/**
 * The actions scorer.
 */
abstract class ActionsScorer<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesType : Features<*, *>,
  in StructureType: ScoringSupportStructure> {

  /**
   * Assign scores to the actions contained into the given [structure] using the features contained in it.
   *
   * @param structure the transition support structure that contains the actions to score
   */
  abstract fun score(
    structure: TransitionSupportStructure<
      StateType, TransitionType, ContextType, ItemType, FeaturesType, StructureType>)
}
