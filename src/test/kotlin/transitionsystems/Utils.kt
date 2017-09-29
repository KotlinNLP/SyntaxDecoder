package transitionsystems


import com.kotlinnlp.transitionsystems.DependencyTree

/**
 *
 */
object Utils {

  /**
   *
   */
  fun buildEmptySentence(): List<Int> = arrayListOf()

  /**
   *
   */
  fun buildSentence5(): List<Int> = arrayListOf(1, 2, 3, 4, 5)

  /**
   *
   */
  fun buildDependencyTree(): DependencyTree {

    val dependencyTree = DependencyTree()

    dependencyTree.setArc(dependentId = 5, governorId = 4)
    dependencyTree.setArc(dependentId = 4, governorId = 3)
    dependencyTree.setArc(dependentId = 3, governorId = 2)
    dependencyTree.setArc(dependentId = 2, governorId = 1)
    dependencyTree.setArc(dependentId = 1, governorId = null)

    return dependencyTree
  }
}
