/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.modules.supportstructure

import com.kotlinnlp.syntaxdecoder.utils.DecodingContext

/**
 * The factory of a [ScoringGlobalSupportStructure] and its related [DecodingContext].
 */
interface SupportStructureFactory<out ScoringGlobalStructureType : ScoringGlobalSupportStructure>
{

  /**
   * Build a new [ScoringGlobalSupportStructure].
   *
   * @return a new scoring global support structure
   */
  fun globalStructure(): ScoringGlobalStructureType
}
