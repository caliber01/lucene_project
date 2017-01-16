package de.unihannover.lucene_task

import java.util.Date

import org.apache.lucene.document.{DateTools, Document}

class SampleDocument(val id: String, val title: String, val text: String, val date: Date) {
  override def toString: String = s"$title $date"

  def this(doc: Document) = {
    this(
      doc.get("id"),
      doc.get("title"),
      doc.get("content"),
      DateTools.stringToDate(doc.get("date"))
    )
  }
}
