/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.helpers.actionsscorer.features

/**
 * The features extracted from a FeaturesExtractor, used as input of the ActionsScorer.
 */
interface Features<out ErrorsType: FeaturesErrors, out RelevanceType: FeaturesRelevance> {

  /**
   * The errors associated to this features.
   */
  val errors: ErrorsType

  /**
   * The relevance associated to this features.
   */
  val relevance: RelevanceType
}
