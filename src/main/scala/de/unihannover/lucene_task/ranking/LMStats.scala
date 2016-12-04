package de.unihannover.lucene_task.ranking

import org.apache.lucene.search.similarities.BasicStats

class LMStats(field: String, var collectionProbability: Float = 0) extends BasicStats(field) {

}

