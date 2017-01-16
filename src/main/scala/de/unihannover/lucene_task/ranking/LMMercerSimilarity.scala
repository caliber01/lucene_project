package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.similarities.{BasicStats, SimilarityBase}

class LMMercerSimilarity(val lambda: Float = 0.1F) extends SimilarityBase {

  override def score(stats: BasicStats, freq: Float, docLen: Float): Float = {
    val documentProbability = freq / docLen
    val collectionProbability = stats.getTotalTermFreq / stats.getNumberOfFieldTokens

    stats.getBoost * Math.log((lambda * documentProbability) + (1 - lambda) * collectionProbability).toFloat
  }

  override def toString: String = ""

}
