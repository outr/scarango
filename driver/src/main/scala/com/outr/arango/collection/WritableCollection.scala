package com.outr.arango.collection

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.core.{ArangoDBCollection, ArangoDBDocuments, CollectionInfo}
import com.outr.arango.{Document, Edge}
import fabric.parse.{Json, JsonWriter}
import fabric.rw._
import fabric.{Value, obj, str}

trait WritableCollection[D <: Document[D]] extends ReadableCollection[D] with ArangoDBDocuments[D] {
  protected def arangoCollection: ArangoDBCollection
  override protected def _collection: ArangoCollectionAsync = arangoCollection._collection

  override protected def toT(json: String): D = {
    val obj = Json.parse(json)
    val mutated = afterRetrieval(obj)
    mutated.as[D]
  }

  override protected def fromT(doc: D): String = {
    val obj = doc.toValue
    val mutated = beforeStorage(obj)
    Json.format(mutated, JsonWriter.Compact)
  }

  protected def beforeStorage(value: Value): Value

  protected def afterRetrieval(value: Value): Value

  object collection {
    def create(): IO[CollectionInfo] = arangoCollection.collection.create(model.collectionOptions)
    def exists(): IO[Boolean] = arangoCollection.collection.exists()
    def truncate(): IO[CollectionInfo] = arangoCollection.collection.truncate()
    def drop(): IO[Unit] = arangoCollection.collection.drop()
    def info(): IO[CollectionInfo] = arangoCollection.collection.info()
  }

  private implicit def rw: ReaderWriter[D] = model.rw
}