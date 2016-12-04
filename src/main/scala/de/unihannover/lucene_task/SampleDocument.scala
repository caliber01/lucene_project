package de.unihannover.lucene_task

import java.util.Date

class SampleDocument(val id: String, val title: String, val text: String, val date: Date) {
  override def toString: String = s"$title $date"
}
