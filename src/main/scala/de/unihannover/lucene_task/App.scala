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

  private def search(query: String) = {
    val indexClient = new IndexClient(indexPath)
    indexClient.search(query) foreach println
  }

  def main(args: Array[String]): Unit = {
    val command = args.head
    if (command == "index") generateIndex()
    else if (command == "search") {
      val searchTerm = args.drop(1).mkString(" ")
      search(searchTerm)
    }
    else {
      println("Unknown command")
    }
  }
}
