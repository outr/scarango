package com.outr.arango.collection

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.core.{ArangoDBCollection, ArangoDBDocuments, CollectionInfo}
import com.outr.arango.Document
import fabric.rw._
import fabric.Json

trait WritableCollection[D <: Document[D]] extends ReadableCollection[D] with ArangoDBDocuments[D] {
  protected def arangoCollection: ArangoDBCollection
  override protected def _collection: ArangoCollectionAsync = arangoCollection._collection

  override def toT(value: Json): D = afterRetrieval(value).as[D]

  override def fromT(t: D): Json = beforeStorage(t.json)

  protected def beforeStorage(value: Json): Json

  protected def afterRetrieval(value: Json): Json

  object collection {
    def create(): IO[CollectionInfo] = arangoCollection.collection.create(model.collectionOptions)
    def exists(): IO[Boolean] = arangoCollection.collection.exists()
    def truncate(): IO[CollectionInfo] = arangoCollection.collection.truncate()
    def drop(): IO[Unit] = arangoCollection.collection.drop()
    def info(): IO[CollectionInfo] = arangoCollection.collection.info()
  }

  private implicit def rw: RW[D] = model.rw
}