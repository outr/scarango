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