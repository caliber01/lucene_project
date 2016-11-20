package de.unihannover.lucene_task

import java.nio.file.Paths

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{DateTools, Document}
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.{IndexSearcher, ScoreDoc}
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.queryparser.classic.QueryParser

class IndexClient(indexDir: String) {
  private val indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)))
  private val indexSearcher = new IndexSearcher(indexReader)
  private val queryParser =  new QueryParser("text", new StandardAnalyzer())

  def extractDoc(scoreDoc: ScoreDoc): SampleDocument = {
    val doc = indexSearcher.doc(scoreDoc.doc)
    val title = doc.getField("title").stringValue
    val rawDate = doc.getField("date").stringValue
    val date = DateTools.stringToDate(rawDate)
    new SampleDocument(title, "", date)
  }

  def search(q: String) = {
    val Array(term, date) = q.split(" @ ")
    val Array(from, to) = date.split("-")
    val rawQuery = s"$term AND date:[$from TO $to]"
    val query = queryParser.parse(rawQuery)
    val topDocs = indexSearcher.search(query, 10)
    topDocs.scoreDocs map extractDoc
  }

  def close() = indexReader.close()
}
