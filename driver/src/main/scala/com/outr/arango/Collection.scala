package com.outr.arango

class Collection[D <: Document[D]](val graph: Graph,
                                   val model: DocumentModel[D]) {
  graph.add(this)

  def name: String = model.collectionName
}
