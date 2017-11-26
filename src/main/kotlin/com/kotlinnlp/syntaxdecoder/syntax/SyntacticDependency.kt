/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.syntax

/**
 * Defines a syntactic dependency through a dependent and its governor.
 */
interface SyntacticDependency {

  /**
   * The dependent id (can be null)
   */
  val dependentId: Int?

  /**
   * The governor id (can be null).
   */
  val governorId: Int?
}
