package de.unihannover.lucene_task

import java.util.Date

class SampleDocument(val title: String, val text: String, val date: Date) {
  override def toString: String = s"$title $date"
}
