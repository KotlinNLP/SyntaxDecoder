/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.state.stateview.StateView
import com.kotlinnlp.transitionsystems.state.DecodingContext

/**
 * The FeaturesExtractor.
 */
interface FeaturesExtractor<
  in StateViewType : StateView,
  ContextType : DecodingContext<ContextType>,
  out FeaturesType : Features<*, *>> {

  /**
   * Extract features using the given [stateView] and an optional [context].
   *
   * @param stateView a view of the state
   * @param context items context (optional)
   *
   * @return the extracted [Features]
   */
  fun extract(stateView: StateViewType, context: ContextType): FeaturesType
}
