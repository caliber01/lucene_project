package de.unihannover.lucene_task

import java.io._
import java.nio.charset.StandardCharsets

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.search.similarities.LMDirichletSimilarity
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import scala.collection.JavaConverters._

import scala.io.Source

object WordEmbeddingsTask {
  val MODEL_PATH = "./models_cache/"
  lazy val indexClient = new IndexClient("./index", new LMDirichletSimilarity(2000))

  private def getModelName(query: String) = query.replaceAll(" ", "_")

  val queries = Map(
    "001a" -> "Causes of stress",
    "002a" -> "Weight loss",
    "003a" -> "AIDS in Africa",
    "004a" -> "Waterborne diseases in Africa",
    "005a" -> "Obesity in children",
    "006a" -> "Diabetes",
    "007a" -> "Hair loss or baldness",
    "008a" -> "English as a second language",
    "009a" -> "Playing guitar",
    "010a" -> "PlayStation 4"
  )

  def run(): Unit = {
    queries.foreach { case (id, query) => testQuery(id, query) }
  }

  private def testQuery(queryId: String, query: String) = {
    val modelName = getModelName(query)
    val fullModelPath = s"$MODEL_PATH/$modelName"
    val modelFile = new File(fullModelPath)

    // read model from disk or create new if one is not present
    val word2vec =
      if (modelFile.exists()) WordVectorSerializer.readWord2Vec(modelFile)
      else writeModel(query, fullModelPath)

    expand(queryId, query, word2vec)
  }

  private def writeModel(query: String, fullModelPath: String) = {
    val limit = 500
    val results = indexClient.search(query, None, limit)

    println("Searching")
    val corpus = results.map(_.text.toLowerCase()).reduceLeft(_ + _)
    val stream = new ByteArrayInputStream(corpus.getBytes(StandardCharsets.UTF_8))

    val inputIterator = new BasicLineIterator(stream)

    val tokenizerFactory = new DefaultTokenizerFactory
    tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor)

    println("Learning word embeddings")
    val word2vec = new Word2Vec.Builder()
      .minWordFrequency(5)
      .iterations(1)
      .layerSize(100)
      .seed(42)
      .windowSize(5)
      .iterate(inputIterator)
      .tokenizerFactory(tokenizerFactory)
      .build()

    word2vec.fit()

    WordVectorSerializer.writeWord2Vec(word2vec, fullModelPath)
    word2vec
  }

  private def removeStopWords(query: String) = {
    val analyzer = new StandardAnalyzer()
    val tokenStream = analyzer.tokenStream(null, new StringReader(query))
    var tokenizedQuery = List[String]()
    tokenStream.reset()
    while (tokenStream.incrementToken()) {
      tokenizedQuery = tokenStream.getAttribute(classOf[CharTermAttribute]).toString :: tokenizedQuery
    }
    tokenizedQuery
  }

  private def expand(queryId: String, query: String, word2vec: Word2Vec) = {
    // remove stopwords from query
    val tokenizedQuery = removeStopWords(query)
    println("tokenized query: ", tokenizedQuery)

    // create expanded queries
    def replaceQueryTerm(index: Int) = {
      val i = index % tokenizedQuery.length
      val term = tokenizedQuery(i)
      val run = index / tokenizedQuery.length + 1
      val similarTerms = word2vec.wordsNearest(term, 10).asScala.toList
      // println("similarTerms", term, similarTerms)
      tokenizedQuery.patch(
        i,
        if (similarTerms.nonEmpty) Seq(similarTerms(run - 1)) else Seq()
        , 1)
    }
    val expandedQueries = Range(0, 5) map replaceQueryTerm
    println("expandedQueries")
    expandedQueries map (_.mkString(" ")) foreach println

    // get original query results
    val judgements = getJudgements(queryId)
    println("RESULT: " + query + ": " + precision(judgements, indexClient.search(query, None, 5)))

    // run a disjunctive query that combines all 5 expanded queries
    val expandedResults = indexClient.search(expandedQueries.map(_.mkString(" ")), None, 5)
    println("EXPENDED RESULT: " + precision(judgements, expandedResults))
  }

  private def getJudgements(queryId: String): Map[String, String] = {
    val rawTruth =
      Source.fromInputStream(
        this.getClass.getClassLoader
          .getResourceAsStream("ground_truth.txt")
      ).mkString

    val truthMap = rawTruth.split("\n")
      .map(_.split(" "))
      .filter { case Array(qId, _, _) => qId == queryId}
      .map { case Array(_, id, judgement) => id -> judgement }
      .toMap

    truthMap
  }

  private def precision(judgements: Map[String, String], documents: Array[SampleDocument]): Float = {
    val relevantCount = documents
      .filter(doc => judgements.contains(doc.id))
      .map(doc => judgements(doc.id))
      .foldLeft(0)((acc, judgement) => if (judgement == "L1" || judgement == "L2") acc + 1 else acc)

    relevantCount / documents.length.toFloat
  }
}
