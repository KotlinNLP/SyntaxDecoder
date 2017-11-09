/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.actionsscorer

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.utils.Updatable
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.modules.featuresextractor.features.FeaturesErrors
import com.kotlinnlp.syntaxdecoder.utils.scheduling.BatchScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.EpochScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.ExampleScheduling
import com.kotlinnlp.syntaxdecoder.context.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.context.items.StateItem
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.TransitionSupportStructure
import com.kotlinnlp.syntaxdecoder.modules.supportstructures.ScoringSupportStructure

/**
 * The trainable [ActionsScorer].
 */
abstract class ActionsScorerTrainable<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  FeaturesErrorsType: FeaturesErrors,
  FeaturesType : Features<FeaturesErrorsType, *>,
  out ScoringStructureType: ScoringSupportStructure,
  in TransitionStructureType : TransitionSupportStructure<StateType, TransitionType, ContextType, ItemType,
    FeaturesType, ScoringStructureType>>
  :
  ActionsScorer<StateType, TransitionType, ContextType, ItemType, FeaturesType, ScoringStructureType, TransitionStructureType>(),
  ExampleScheduling,
  BatchScheduling,
  EpochScheduling,
  Updatable {

  /**
   * Backward errors through this [ActionsScorer], starting from the scored actions of the given [structure].
   * Errors are required to be already set into the output actions properly.
   *
   * @param structure the dynamic support structure that contains the scored actions
   * @param propagateToInput a Boolean indicating whether errors must be propagated to the input items
   */
  abstract fun backward(structure: TransitionStructureType, propagateToInput: Boolean)

  /**
   * @param structure the dynamic support structure that contains the scored actions
   *
   * @return the errors of the features used to score the actions of the given [structure]
   */
  abstract fun getFeaturesErrors(structure: TransitionStructureType): FeaturesErrorsType
}
