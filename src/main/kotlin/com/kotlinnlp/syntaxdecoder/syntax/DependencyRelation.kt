/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.syntax

import com.kotlinnlp.dependencytree.Deprel

/**
 * Extends a syntactic dependency with the relation between the dependent and its governor.
 */
interface DependencyRelation : SyntacticDependency {

  /**
   * The Dependency Relation (can be null)
   */
  var deprel: Deprel?
}
