package com.outr.arango.collection

import com.outr.arango.core.ArangoDBCollection
import com.outr.arango.{CollectionType, Document, DocumentModel, Graph}
import fabric.Json

class DocumentCollection[D <: Document[D]](protected[arango] val graph: Graph,
                                           protected[arango] val arangoCollection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType) extends WritableCollection[D] {
  override def dbName: String = graph.databaseName
  override def name: String = arangoCollection.name
  override lazy val query: DocumentCollectionQuery[D] = new DocumentCollectionQuery[D](this)

  override protected def beforeStorage(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.store(v))

  override protected def afterRetrieval(value: Json): Json = model.allMutations.foldLeft(value)((v, m) => m.retrieve(v))
}