import java.io.File

import de.unihannover.lucene_task.DocumentsParser

import scala.io.Source

val filename = "/home/maksym/Workspace/lucene_task/src/main/resources/documents/fixed.xml"
val file = new File(filename)
file.canRead
Source.fromFile(filename).mkString
val docs = new DocumentsParser().parseCollection(file)
docs.length
val doc = docs.head
doc.title
doc.date
