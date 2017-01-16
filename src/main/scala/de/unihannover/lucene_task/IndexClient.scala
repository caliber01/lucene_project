package de.unihannover.lucene_task

import java.nio.file.Paths
import java.util.Calendar

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{DateTools, LongPoint}
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.search._
import org.apache.lucene.store.FSDirectory

class IndexClient(indexDir: String, similarity: Similarity) {
  private val indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)))
  private val indexSearcher = {
    val indexSearcher = new IndexSearcher(indexReader)
    indexSearcher.setSimilarity(similarity)
    indexSearcher
  }
  private val queryParser =  new QueryParser("content", new StandardAnalyzer())

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

  def search(q: String, temporalFilter: Option[String], k: Int): Array[SampleDocument] =
    search(Array(q), temporalFilter, k)

  def search(q: Iterable[String], temporalFilter: Option[String], k: Int): Array[SampleDocument] = {
    val queryBuilder = new BooleanQuery.Builder()

    if (temporalFilter.isDefined) {
      val Array(fromYear, toYear) = temporalFilter.get.split("-")
      val from = prepareDate(fromYear, alignLowerBound = true)
      val to = prepareDate(toYear, alignLowerBound = false)

      val datePredicate = LongPoint.newRangeQuery("date", from, to)

      queryBuilder.add(datePredicate, BooleanClause.Occur.MUST)
    }

    val queriesPredicate = q.map(term => queryParser.parse(term))
      .foldLeft(new BooleanQuery.Builder())((builder, termPredicate) => builder.add(termPredicate, BooleanClause.Occur.SHOULD))
      .build()

    queryBuilder.add(queriesPredicate, BooleanClause.Occur.MUST)

    val query = queryBuilder.build()
    val topDocs = indexSearcher.search(query, k)
    topDocs.scoreDocs.map(scoreDoc => new SampleDocument(indexSearcher.doc(scoreDoc.doc)))
  }

  def close(): Unit = indexReader.close()
}
