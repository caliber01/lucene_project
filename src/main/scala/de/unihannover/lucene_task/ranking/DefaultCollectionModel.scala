package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.similarities.BasicStats

class DefaultCollectionModel {

  def computeProbability(stats: BasicStats): Float =
    (stats.getTotalTermFreq + 1F) / (stats.getNumberOfFieldTokens + 1F)

}

