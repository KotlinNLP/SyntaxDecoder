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
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext
import com.kotlinnlp.transitionsystems.state.State

/**
 * The ActionsScorer that implements [Trainable].
 *
 * @property featuresExtractor a [FeaturesExtractor]
 */
abstract class ActionsScorerTrainable<
  StateType : State<StateType>,
  TransitionType : Transition<TransitionType, StateType>,
  out FeaturesErrorsType : FeaturesErrors,
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<FeaturesErrorsType, *>>
(
  featuresExtractor: FeaturesExtractor<StateViewType, ContextType, FeaturesType>
) :
  ActionsScorer<StateType, TransitionType, StateViewType, ContextType, FeaturesType>(featuresExtractor),
  Trainable<FeaturesErrorsType>
