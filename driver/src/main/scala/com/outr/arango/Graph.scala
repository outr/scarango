package com.outr.arango

import cats.effect.IO
import com.outr.arango.core.{ArangoDB, ArangoDBCollection, ArangoDBConfig, ArangoDBDocuments, ArangoDBServer, CollectionInfo}
import fabric.rw._

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
                                           protected val collection: ArangoDBCollection,
                                           val model: DocumentModel[D],
                                           val `type`: CollectionType) extends WritableCollection[D] {

}

trait WritableCollection[D <: Document[D]] extends Collection[D] {
  protected def collection: ArangoDBCollection

  def create(): IO[CollectionInfo] = collection.create(model.collectionOptions)
  def exists(): IO[Boolean] = collection.exists()
  def drop(): IO[Unit] = collection.drop()
  def info(): IO[CollectionInfo] = collection.info()

  private implicit def rw: ReaderWriter[D] = model.rw

  private def string2Doc(json: String): D = fabric.parse.Json.parse(json).as[D]
  private def doc2String(doc: D): String = fabric.parse.Json.format(doc.toValue)

  lazy val document = new ArangoDBDocuments[D](collection.collection, string2Doc, doc2String)
}

trait Collection[D <: Document[D]] {
  def model: DocumentModel[D]
  def `type`: CollectionType
}