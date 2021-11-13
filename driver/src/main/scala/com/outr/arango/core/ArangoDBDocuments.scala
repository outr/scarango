package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.util.Helpers._

import scala.jdk.CollectionConverters._

class ArangoDBDocuments[T](collection: ArangoCollectionAsync, toT: String => T, fromT: T => String) {
  def insert(doc: T, options: CreateOptions = CreateOptions.Insert): IO[CreateResult[T]] = collection
    .insertDocument(fromT(doc), options)
    .toIO
    .map(createDocumentEntityConversion(_, toT))

  def upsert(doc: T, options: CreateOptions = CreateOptions.Upsert): IO[CreateResult[T]] = insert(doc, options)

  def update(key: String, doc: T, options: UpdateOptions): IO[UpdateResult[T]] = collection
    .updateDocument(key, fromT(doc), options)
    .toIO
    .map(updateDocumentEntityConversion(_, toT))

  def delete(key: String, options: DeleteOptions = DeleteOptions.Default): IO[DeleteResult[T]] = collection
    .deleteDocument(key, classOf[String], options)
    .toIO
    .map(deleteDocumentEntityConversion(_, toT))

  object batch {
    def insert(docs: List[T], options: CreateOptions = CreateOptions.Insert): IO[CreateResults[T]] = collection
      .insertDocuments(docs.map(fromT).asJava, options)
      .toIO
      .map(multiDocumentCreateConversion(_, toT))

    def upsert(docs: List[T], options: CreateOptions = CreateOptions.Upsert): IO[CreateResults[T]] = insert(docs, options)

    def delete(docs: List[T], options: DeleteOptions = DeleteOptions.Default): IO[DeleteResults[T]] = collection
      .deleteDocuments(docs.map(fromT).asJava, classOf[String], options)
      .toIO
      .map(multiDocumentDeleteConversion(_, toT))
  }
}