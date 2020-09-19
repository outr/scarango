package com.outr.arango.monitored

import com.outr.arango.api.OperationType
import com.outr.arango.{Collection, Document, Query}

case class Reference[D <: Document[D]](collection: Collection[D],
                                       addedQuery: GetReferences[D] => Query,
                                       removedQuery: GetReferences[D] => Query) {
  def connect[Base <: Document[Base], Into <: Document[Into]](materialized: Materialized[Base, Into]): Unit = {
    val monitor = materialized.graph.monitor(collection)
    monitor.attach { op =>
      op._id.foreach { id =>
        if (op.`type` == OperationType.InsertReplaceDocument) {
          val refQuery = addedQuery(GetReferences(materialized.ids, id))
          materialized.update(refQuery)
        } else if (op.`type` == OperationType.RemoveDocument) {
          val refQuery = removedQuery(GetReferences(materialized.ids, id))
          materialized.update(refQuery)
        }
      }
    }
  }
}

object Reference {
  def merge[D <: Document[D]](references: List[Reference[D]]): Reference[D] = if (references.isEmpty) {
    throw new RuntimeException("No references to merge")
  } else if (references.tail.isEmpty) {
    references.head
  } else {
    val addedQuery = (get: GetReferences[D]) => {
      val queries = references.map(_.addedQuery(get))
      Query.merge(queries)
    }
    val removedQuery = (get: GetReferences[D]) => {
      val queries = references.map(_.removedQuery(get))
      Query.merge(queries)
    }
    Reference[D](references.head.collection, addedQuery, removedQuery)
  }
}