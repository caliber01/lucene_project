package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.{CollectionStatistics, TermStatistics}
import org.apache.lucene.search.similarities.{BasicStats, SimilarityBase}

abstract class LMSimilarity(val collectionModel: DefaultCollectionModel = new DefaultCollectionModel) extends SimilarityBase {

  override def newStats(field: String): BasicStats = new LMStats(field)

  override def fillBasicStats(stats: BasicStats, collectionStats: CollectionStatistics, termStats: TermStatistics): Unit = {
    super.fillBasicStats(stats, collectionStats, termStats)
    val lmStats = stats.asInstanceOf[LMStats]
    lmStats.collectionProbability = collectionModel.computeProbability(stats)
  }

}
