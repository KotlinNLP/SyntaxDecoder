/* Copyright 2017-present The KotlinNLP Authors. All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * ------------------------------------------------------------------*/

package com.kotlinnlp.transitionsystems.state

import com.kotlinnlp.transitionsystems.Oracle
import com.kotlinnlp.transitionsystems.Transition
import com.kotlinnlp.transitionsystems.state.items.StateItem
import com.kotlinnlp.transitionsystems.utils.Clonable

/**
 * The [ExtendedState] extends a [State] with the list of [StateItem]s that compose it and a [DecodingContext].
 *
 * This structure allows you to keep aligned with state the properties that can evolve together with it.
 *
 * @property state a [State]
 * @property context a [DecodingContext]
 * @property oracle an [Oracle] (optional)
 */
data class ExtendedState<
  StateType : State<StateType>,
  TransitionType: Transition<TransitionType, StateType>,
  ItemType : StateItem<ItemType, *, *>,
  ContextType : DecodingContext<ContextType, ItemType>>(
  val state: StateType,
  val context: ContextType,
  val oracle: Oracle<StateType, TransitionType>?
) : Clonable<ExtendedState<StateType, TransitionType, ItemType, ContextType>> {

  /**
   * @return a copy of this [ExtendedState]
   */
  override fun copy(): ExtendedState<StateType, TransitionType, ItemType, ContextType> {

    return ExtendedState(
      state = this.state.copy(),
      context = this.context.copy(),
      oracle = this.oracle?.copy())
  }
}
