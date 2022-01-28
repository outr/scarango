package com.outr.arango.collection

import com.outr.arango.core.ArangoDBCollection
import com.outr.arango.{CollectionType, Document, DocumentModel, Graph}

class DocumentCollection[D <: Document[D]](protected[arango] val graph: Graph,
                                           protected[arango] val arangoCollection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType) extends WritableCollection[D] {
  override def dbName: String = graph.databaseName
  override def name: String = arangoCollection.name
  override lazy val query: DocumentCollectionQuery[D] = new DocumentCollectionQuery[D](this)
}