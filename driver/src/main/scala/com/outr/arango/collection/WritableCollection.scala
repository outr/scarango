package com.outr.arango.collection

import cats.effect.IO
import com.outr.arango.core.{ArangoDBCollection, ArangoDBDocuments, CollectionInfo}
import com.outr.arango.{Document, Edge}
import fabric.rw._
import fabric.{obj, str}

trait WritableCollection[D <: Document[D]] extends ReadableCollection[D] {
  protected def collection: ArangoDBCollection

  def create(): IO[CollectionInfo] = collection.create(model.collectionOptions)

  def exists(): IO[Boolean] = collection.exists()

  def truncate(): IO[CollectionInfo] = collection.truncate()

  def drop(): IO[Unit] = collection.drop()

  def info(): IO[CollectionInfo] = collection.info()

  private implicit def rw: ReaderWriter[D] = model.rw

  private def string2Doc(json: String): D = fabric.parse.Json.parse(json).as[D]

  private def doc2String(doc: D): String = {
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

  lazy val document = new ArangoDBDocuments[D](collection.collection, string2Doc, doc2String)
}
