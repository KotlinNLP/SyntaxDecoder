/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer

import com.kotlinnlp.transitionsystems.helpers.actionsscorer.features.Features
import com.kotlinnlp.transitionsystems.helpers.actionsscorer.stateview.StateView
import com.kotlinnlp.transitionsystems.state.ItemsContext

/**
 * The FeaturesExtractor.
 */
interface FeaturesExtractor<in StateViewType : StateView, out FeaturesType : Features<*, *>> {

  /**
   * Extract features using the given [stateView] and an optional [itemsContext].
   *
   * @param stateView a view of the state
   * @param itemsContext items context (optional)
   *
   * @return the extracted [Features]
   */
  fun extract(stateView: StateViewType, itemsContext: ItemsContext<*>?): FeaturesType
}
