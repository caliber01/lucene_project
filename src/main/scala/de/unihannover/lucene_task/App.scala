package de.unihannover.lucene_task

import java.nio.file.Paths

import de.unihannover.lucene_task.ranking.{LMDirichletsSimilarity, LMMercerSimilarity}
import org.apache.lucene.search.similarities.{ClassicSimilarity, Similarity}

import scala.util.Try


object App {
  private val collectionPath = getClass.getClassLoader.getResource("documents").getFile

  def generateIndex(similarity: Similarity, indexPath: String): Unit = {
    val indexGenerator = new IndexGenerator(
      Paths.get(indexPath).toAbsolutePath.toString,
      similarity
    )
    indexGenerator.index(collectionPath, file => file.getName.endsWith("xml"))
    indexGenerator.close()
  }

  def search(query: String, k: Int, similarity: Similarity = new ClassicSimilarity(), indexPath: String) = {
    val indexClient = new IndexClient(indexPath, similarity)
    indexClient.search(query, k)
  }

  // usage search dirichlet 2000 50 america@ 2000-2012
  def main(args: Array[String]): Unit = {
    val command = args.head
    val similarityName = args(1)
    val parameter = Try(args(2).toFloat).toOption

    val similarity = if (similarityName == "dirichlet") {
      println("Using LMDirichletSimilarity")
      parameter.map(new LMDirichletsSimilarity(_)).getOrElse(new LMDirichletsSimilarity())
    } else {
      println("Using LMMercerSimilarity")
      parameter.map(new LMMercerSimilarity(_)).getOrElse(new LMMercerSimilarity())
    }

    if (command == "index") generateIndex(similarity, s"./$similarityName")
    else if (command == "search") {
      val indexName = s"./$similarityName"
      val records = args(3).toInt
      val searchTerm = args.drop(4).mkString(" ")
      println(searchTerm)
      search(searchTerm, records, similarity, indexName)
          .map { case (id, _) => id } foreach println
    }
    else {
      throw new IllegalArgumentException("Unknown command")
    }
  }
}
