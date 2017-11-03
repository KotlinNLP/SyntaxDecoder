package com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington

import com.kotlinnlp.syntaxdecoder.transitionsystem.TransitionsGenerator
import com.kotlinnlp.syntaxdecoder.transitionsystem.models.covington.transitions.*

/**
 * The CovingtonTransitionsGenerator for the Covington Transition System.
 */
class CovingtonTransitionsGenerator : TransitionsGenerator<CovingtonState, CovingtonTransition> {

  /**
   * @param state the state from which to extract valid transitions.
   *
   * @return a list of valid transitions for the given [state].
   */
  override fun generate(state: CovingtonState): List<CovingtonTransition>  = listOf(
    Shift(state),
    NoArc(state),
    ArcLeft(state, dependentStack1Index = 1),
    ArcRight(state, governorStack1Index = 1)
  ).filter { it.isAllowed }
}