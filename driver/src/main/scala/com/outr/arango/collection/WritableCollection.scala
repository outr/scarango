package com.outr.arango.collection

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.core.{ArangoDBCollection, ArangoDBDocuments, CollectionInfo}
import com.outr.arango.{Document, Edge}
import fabric.rw._
import fabric.{obj, str}

trait WritableCollection[D <: Document[D]] extends ReadableCollection[D] with ArangoDBDocuments[D] {
  protected def arangoCollection: ArangoDBCollection
  override protected def _collection: ArangoCollectionAsync = arangoCollection._collection
  override protected def toT(json: String): D = fabric.parse.Json.parse(json).as[D]

  override protected def fromT(doc: D): String = {
    val keys = doc match {
      case edge: Edge[_, _, _] => obj(
        "_key" -> str(edge._id.value),
        "_from" -> str(edge._from._id),
        "_to" -> str(edge._to._id)
      )
      case _ => obj("_key" -> str(doc._id.value))
    }
    val value = doc.toValue.merge(keys)
    fabric.parse.Json.format(value)
  }

  object collection {
    def create(): IO[CollectionInfo] = arangoCollection.collection.create(model.collectionOptions)
    def exists(): IO[Boolean] = arangoCollection.collection.exists()
    def truncate(): IO[CollectionInfo] = arangoCollection.collection.truncate()
    def drop(): IO[Unit] = arangoCollection.collection.drop()
    def info(): IO[CollectionInfo] = arangoCollection.collection.info()
  }

  private implicit def rw: ReaderWriter[D] = model.rw
}
