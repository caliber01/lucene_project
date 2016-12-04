package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.similarities.BasicStats

class LMMercerSimilarity(val lambda: Float = 0.1F) extends LMSimilarity {

  override def score(stats: BasicStats, freq: Float, docLen: Float): Float = {
    val basicStats = stats.asInstanceOf[LMStats]
    stats.getBoost *
      Math.log(1 +
        ((1 - lambda) * freq / docLen) /
          (lambda * basicStats.collectionProbability)).toFloat
  }

  override def toString: String = ""

}
