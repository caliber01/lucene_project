package de.unihannover.lucene_task

import java.nio.file.Paths

object App {
  private val indexPath = "./index"
  private val collectionPath = getClass.getClassLoader.getResource("documents").getFile

  private def generateIndex() = {
    val indexGenerator = new IndexGenerator(Paths.get(indexPath).toAbsolutePath.toString)
    indexGenerator.index(collectionPath, file => file.getName.endsWith("xml"))
    indexGenerator.close()
  }

  def search(query: String): Int = {
    val indexClient = new IndexClient(indexPath)
    indexClient.search(query)
  }

  def numDocs: Int = new IndexClient(indexPath).numDocs()

  def main(args: Array[String]): Unit = {
    val command = args.head
    if (command == "index") generateIndex()
    else if (command == "search") {
      val searchTerm = args.drop(1).mkString(" ")
      println(search(searchTerm))
    }
    else {
      throw new IllegalArgumentException("Unknown command")
    }
  }
}
