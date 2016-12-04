package de.unihannover.lucene_task

import java.io.{File, FileFilter}
import java.nio.file.Paths

import de.unihannover.lucene_task.ranking.LMDirichletsSimilarity
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document._
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.store.FSDirectory

class IndexGenerator(indexFolder: String, similarity: Similarity) {
  private val writer = {
    val indexDir = FSDirectory.open(Paths.get(indexFolder))
    val analyzer = new StandardAnalyzer()
    val config = new IndexWriterConfig(analyzer)
    config.setSimilarity(similarity)
    config.setOpenMode(OpenMode.CREATE)
    new IndexWriter(indexDir, config)
  }

  private def getDocument(sampleDocument: SampleDocument) = {
    val doc = new Document()
    doc.add(new StringField("id", sampleDocument.id, Field.Store.YES))
    doc.add(new TextField("content", s"${sampleDocument.title} ${sampleDocument.text}", Field.Store.YES))
    doc.add(new LongPoint(
      "date",
      sampleDocument.date.getTime
    ))
    doc
  }

  def index(dataDir: String, filter: FileFilter): Int = {
    val documentsParser = new DocumentsParser()
    new File(dataDir)
      .listFiles
      .filter(filter.accept)
      .flatMap(documentsParser.parseCollection)
      .foreach(sampleDocument => writer.addDocument(getDocument(sampleDocument)))
    writer.numDocs
  }

  def close() = writer.close()
}
