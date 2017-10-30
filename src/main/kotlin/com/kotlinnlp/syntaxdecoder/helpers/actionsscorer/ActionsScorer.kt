/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.helpers.actionsscorer

import com.kotlinnlp.syntaxdecoder.transitionsystem.state.State
import com.kotlinnlp.syntaxdecoder.transitionsystem.Transition
import com.kotlinnlp.syntaxdecoder.helpers.featuresextractor.features.Features
import com.kotlinnlp.syntaxdecoder.DecodingContext
import com.kotlinnlp.syntaxdecoder.items.StateItem
import com.kotlinnlp.syntaxdecoder.transitionsystem.state.stateview.StateView

/**
 * The actions scorer.
 */
abstract class ActionsScorer<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  ContextType : DecodingContext<ContextType, ItemType>,
  ItemType : StateItem<ItemType, *, *>,
  out StateViewType : StateView<StateType>,
  in FeaturesType : Features<*, *>,
  StructureType: ActionsScorerStructure<StructureType, StateType, TransitionType, ContextType, ItemType>> {

  /**
   * Assign scores to the actions contained into the given [structure], using the given [features].
   *
   * @param features the features used to score actions
   * @param structure the dynamic support structure that contains the actions to score
   */
  abstract fun score(
    features: FeaturesType,
    structure: ActionsScorerMemory<StateType, TransitionType, ContextType, ItemType, StructureType>)

  /**
   * @return a support structure for this [ActionsScorer]
   */
  abstract fun supportStructureFactory(): StructureType

  /**
   * @param structure the dynamic support structure used to build a state view
   *
   * @return the state view of the given structure, used by the features extractor
   */
  abstract fun buildStateView(
    structure: ActionsScorerMemory<StateType, TransitionType, ContextType, ItemType, StructureType>
  ): StateViewType

  /**
   * @return a map of Transitions to their related Actions
   */
  protected fun List<Transition<TransitionType, StateType>.Action>.mapToTransitions():
    Map<Transition<TransitionType, StateType>, List<Transition<TransitionType, StateType>.Action>>
    = this.groupBy { it.transition }
}
