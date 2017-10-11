/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state

/**
 * The StateContext is a wrapper of the context of a [State].
 *
 * @property state the [State] of this context
 */
interface StateContext<
  SelfType: StateContext<SelfType, StateType, ItemType>,
  StateType: State<StateType>,
  ItemType: StateItem<*, *>> {

  /**
   * The [State] of this context.
   */
  val state: StateType

  /**
   * The list of items that compose the [state].
   */
  val items: List<ItemType>

  /**
   * @return a copy of this [StateContext]
   */
  fun clone(): SelfType
}
