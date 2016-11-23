package de.unihannover.lucene_task

import java.nio.file.Paths
import java.util.Calendar

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{DateTools, LongPoint}
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{BooleanClause, BooleanQuery, IndexSearcher, ScoreDoc}
import org.apache.lucene.store.FSDirectory

class IndexClient(indexDir: String) {
  private val indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)))
  private val indexSearcher = new IndexSearcher(indexReader)
  private val queryParser =  new QueryParser("content", new StandardAnalyzer())

  def extractDoc(scoreDoc: ScoreDoc): SampleDocument = {
    val doc = indexSearcher.doc(scoreDoc.doc)
    val title = doc.getField("title").stringValue
    val rawDate = doc.getField("date").stringValue
    val date = DateTools.stringToDate(rawDate)
    new SampleDocument(title, "", date)
  }

  def numDocs(): Int = indexReader.numDocs()

  private def prepareDate(year: String, alignLowerBound: Boolean) = {
    def alignBottom(calendar: Calendar, field: Int) = calendar.set(field, calendar.getActualMinimum(field))
    def alignTop(calendar: Calendar, field: Int) = calendar.set(field, calendar.getActualMaximum(field))

    val calendar = Calendar.getInstance()

    calendar.set(Calendar.YEAR, year.toInt)

    if (alignLowerBound) {
      alignBottom(calendar, Calendar.MONTH)
      alignBottom(calendar, Calendar.DAY_OF_MONTH)
      alignBottom(calendar, Calendar.HOUR_OF_DAY)
      alignBottom(calendar, Calendar.MINUTE)
      alignBottom(calendar, Calendar.SECOND)
      alignBottom(calendar, Calendar.MILLISECOND)
    } else {
      alignTop(calendar, Calendar.MONTH)
      alignTop(calendar, Calendar.DAY_OF_MONTH)
      alignTop(calendar, Calendar.HOUR_OF_DAY)
      alignTop(calendar, Calendar.MINUTE)
      alignTop(calendar, Calendar.SECOND)
      alignTop(calendar, Calendar.MILLISECOND)
    }
    calendar.getTimeInMillis
  }

  def search(q: String): Int = {
    val Array(term, date) = q.split("@ ")
    val Array(fromYear, toYear) = date.split("-")
    val from = prepareDate(fromYear, alignLowerBound = true)
    val to = prepareDate(toYear, alignLowerBound = false)

    val datePredicate = LongPoint.newRangeQuery("date", from, to)
    val termPredicate = queryParser.parse(term)

    val query = new BooleanQuery.Builder()
      .add(termPredicate, BooleanClause.Occur.MUST)
      .add(datePredicate, BooleanClause.Occur.MUST)
      .build()

    val topDocs = indexSearcher.search(query, 10)
    topDocs.totalHits
  }

  def close(): Unit = indexReader.close()
}
