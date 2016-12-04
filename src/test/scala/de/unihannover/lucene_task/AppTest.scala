package de.unihannover.lucene_task

import de.unihannover.lucene_task.ranking.{LMDirichletsSimilarity, LMMercerSimilarity}
import org.apache.lucene.search.similarities.{LMDirichletSimilarity, LMJelinekMercerSimilarity, Similarity}
import org.scalatest.FunSuite

class AppTest extends FunSuite {

  test("index has correct number of documents") {
    assert(App.numDocs == 55927)
  }

  /*test("search gives correct number of hits") {
    assert(App.search("america@ 2011-2013").length == 8387)
    assert(App.search("china@ 2011-2013").length == 5227)
    assert(App.search("republic of ireland@ 2011-2011").length == 1426)
    assert(App.search("diabetes in young children@ 2011-2012").length == 18127)
    assert(App.search("diabetes in young children@ 2011-2009").length == 0)
  }*/

  def testSimilarities(originalSimilarity: Similarity, customSimilarity: Similarity) = {
    val originalIndexPath = "./original_index"
    println("Generating index with original similarity...")
    App.generateIndex(originalSimilarity, originalIndexPath)
    val originalResults = App.search("america@ 2011-2013", 10, originalSimilarity, originalIndexPath)

    val customIndexPath = "./custom_index"
    println("Generating index with custom similarity...")
    App.generateIndex(customSimilarity, customIndexPath)
    val customResults = App.search("america@ 2011-2013", 10, customSimilarity, customIndexPath)

    (originalResults, customResults)
  }

  test("mercer similarity gives correct results") {
    val originalSimilarity = new LMJelinekMercerSimilarity(0.1F)
    val customSimilarity = new LMMercerSimilarity(0.1F)

    val (originalResults, customResults) = testSimilarities(originalSimilarity, customSimilarity)
    assert(originalResults.sameElements(customResults))
  }

  test("dirichlet similarity gives correct results") {
    val originalSimilarity = new LMDirichletSimilarity(2000)
    val customSimilarity = new LMDirichletsSimilarity(2000)

    val (originalResults, customResults) = testSimilarities(originalSimilarity, customSimilarity)
    assert(originalResults.sameElements(customResults))
  }

}
