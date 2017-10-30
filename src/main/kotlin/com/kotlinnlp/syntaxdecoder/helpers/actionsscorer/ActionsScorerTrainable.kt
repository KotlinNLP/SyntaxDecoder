/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.helpers.actionsscorer

import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.utils.Updatable
import com.kotlinnlp.syntaxdecoder.helpers.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.helpers.featuresextractor.features.FeaturesErrors
import com.kotlinnlp.syntaxdecoder.utils.scheduling.BatchScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.EpochScheduling
import com.kotlinnlp.syntaxdecoder.utils.scheduling.ExampleScheduling
import com.kotlinnlp.syntaxdecoder.DecodingContext
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView

/**
 * The trainable [ActionsScorer].
 */
abstract class ActionsScorerTrainable<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  out StateViewType : StateView<StateType>,
  FeaturesErrorsType: FeaturesErrors,
  in FeaturesType : Features<FeaturesErrorsType, *>,
  StructureType: ActionsScorerStructure<StructureType, StateType, TransitionType, ContextType, ItemType>>
  :
  ActionsScorer<StateType, TransitionType, ContextType, ItemType, StateViewType, FeaturesType, StructureType>(),
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
  abstract fun backward(
    structure: ActionsScorerMemory<StateType, TransitionType, ContextType, ItemType, StructureType>,
    propagateToInput: Boolean)

  /**
   * @param structure the dynamic support structure that contains the scored actions
   *
   * @return the errors of the features used to score the actions of the given [structure]
   */
  abstract fun getFeaturesErrors(
    structure: ActionsScorerMemory<StateType, TransitionType, ContextType, ItemType, StructureType>
  ): FeaturesErrorsType
}
