package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.core.ArangoDBCollection
import com.outr.arango.query.Query
import com.outr.arango.{CollectionType, Document, DocumentModel, Graph}

class DocumentCollection[D <: Document[D]](protected[arango] val graph: Graph,
                                           protected[arango] val collection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType) extends WritableCollection[D] {
  override def dbName: String = graph.databaseName
  override def name: String = collection.name
  override def query(query: Query): fs2.Stream[IO, D] = graph.queryAs[D](query)(model.rw)
}