package com.outr.arango

import com.outr.arango.core.{ArangoDB, ArangoDBCollection, ArangoDBConfig, ArangoDBServer}

class Graph(db: ArangoDB) {
  private var _collections: List[Collection[_]] = Nil

  def this(name: String, server: ArangoDBServer) = {
    this(server.db(name))
  }

  def this(name: String, config: ArangoDBConfig) = {
    this(name, ArangoDBServer(config))
  }

  def this(name: String) = {
    this(name, ArangoDBConfig())
  }

  def databaseName: String = db.name

  def vertex[D <: Document[D]](model: DocumentModel[D]): DocumentCollection[D] =
    new DocumentCollection[D](this, db.collection(model.collectionName), model, CollectionType.Vertex)
  def edge[D <: Document[D]](model: DocumentModel[D]): DocumentCollection[D] =
    new DocumentCollection[D](this, db.collection(model.collectionName), model, CollectionType.Edge)

  def collection[D <: Document[D]](model: DocumentModel[D], `type`: CollectionType): DocumentCollection[D] = synchronized {
    val c = new DocumentCollection[D](this, db.collection(model.collectionName), model, `type`)

    c
  }
}

class DocumentCollection[D <: Document[D]](graph: Graph,
                                           collection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType) extends Collection[D] {
}

trait Collection[D <: Document[D]] {
  def model: DocumentModel[D]
  def `type`: CollectionType
}