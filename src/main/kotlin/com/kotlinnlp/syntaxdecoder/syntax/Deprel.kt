/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.syntaxdecoder.syntax

/**
 * The Deprel.
 *
 * @property label
 * @property direction
 */
open class Deprel(val label: String, val direction: Position = Position.NULL){

  /**
   *
   */
  enum class Position {
    ROOT,
    LEFT,
    RIGHT,
    NULL
  }

  /**
   *
   */
  override fun toString(): String = this.label + ":" + this.direction

  /**
   *
   */
  override fun equals(other: Any?): Boolean {
    return if (other is Deprel){
      other.label == this.label && other.direction == this.direction
    } else {
      false
    }
  }

  /**
   *
   */
  override fun hashCode(): Int {
    return this.label.hashCode() * 31 + this.direction.hashCode()
  }
}
