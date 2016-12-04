package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.similarities.BasicStats


class LMDirichletsSimilarity(val mu: Float = 2000) extends LMSimilarity {

  override def score(stats: BasicStats, freq: Float, docLen: Float): Float = {
    val basicStats = stats.asInstanceOf[LMStats]
    val score = stats.getBoost * (Math.log(1 + freq /
      (mu * basicStats.collectionProbability)) +
      Math.log(mu / (docLen + mu))).toFloat
    if (score > 0.0f) score else 0.0f
  }

  override def toString: String = ""

}
