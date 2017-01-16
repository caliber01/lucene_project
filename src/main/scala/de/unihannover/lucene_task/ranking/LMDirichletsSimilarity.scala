package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.similarities.{BasicStats, SimilarityBase}


class LMDirichletsSimilarity(val mu: Float = 2000) extends SimilarityBase {

  override def score(stats: BasicStats, termFreq: Float, docLen: Float): Float = {
    val collectionProbability = stats.getTotalTermFreq / stats.getNumberOfFieldTokens

    stats.getBoost * Math.log((termFreq + mu * collectionProbability) / (docLen + mu)).toFloat
  }

  override def toString: String = ""

}
