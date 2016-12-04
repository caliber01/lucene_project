package de.unihannover.lucene_task

import java.io.File
import java.text.SimpleDateFormat

import scala.xml.{Node, XML}

class DocumentsParser() {
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  private def parseDocument(node: Node): SampleDocument = {
    def getMetaTag(name: String) = ((node \\ "tag") find (node => (node \ "@name").text == name)).get.text

    val id = (node \ "@id").text
    val title = getMetaTag("title")
    val rawDate = getMetaTag("date")
    val date = dateFormat.parse(rawDate)
    val text = (node \\ "text").text

    new SampleDocument(id, title, text, date)
  }

  def parseCollection(file: File) = {
    val xml = XML.loadFile(file)
    val documents = xml \ "doc"
    (documents map parseDocument).view
  }

}
