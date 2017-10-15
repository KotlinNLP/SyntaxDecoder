/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state

import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.utils.Clonable

/**
 * The [ExtendedState] extends a [State] with the list of [StateItem]s that compose it and a [DecodingContext].
 */
data class ExtendedState<
  StateType : State<StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType>>(
  val state: StateType,
  val items: List<ItemType>,
  val context: ContextType
) : Clonable<ExtendedState<StateType, ItemType, ContextType>> {

  /**
   * @return a copy of this [ExtendedState]
   */
  override fun copy(): ExtendedState<StateType, ItemType, ContextType> {

    return ExtendedState(
      state = this.state.copy(),
      items = this.items.map { it.copy() },
      context = this.context.copy())
  }
}
