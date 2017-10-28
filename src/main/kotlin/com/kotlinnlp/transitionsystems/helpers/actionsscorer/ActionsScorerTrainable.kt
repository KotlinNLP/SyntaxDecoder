/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.FeaturesErrors
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.BatchScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.EpochScheduling
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.scheduling.ExampleScheduling
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.State
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.state.stateview.StateView

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
    structure: ActionsScorerDynamicStructure<StateType, TransitionType, ContextType, ItemType, StructureType>,
    propagateToInput: Boolean)

  /**
   * @param structure the dynamic support structure that contains the scored actions
   *
   * @return the errors of the features used to score the actions of the given [structure]
   */
  abstract fun getFeaturesErrors(
    structure: ActionsScorerDynamicStructure<StateType, TransitionType, ContextType, ItemType, StructureType>
  ): FeaturesErrorsType
}
