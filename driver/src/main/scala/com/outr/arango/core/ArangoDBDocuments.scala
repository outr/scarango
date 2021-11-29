package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.util.Helpers._

import scala.jdk.CollectionConverters._

class ArangoDBDocuments[T](collection: ArangoCollectionAsync, toT: String => T, fromT: T => String) {
  def insert(doc: T, options: CreateOptions = CreateOptions.Insert, transaction: StreamTransaction = None.orNull): IO[CreateResult[T]] = collection
    .insertDocument(fromT(doc), options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(createDocumentEntityConversion(_, toT))

  def upsert(doc: T, options: CreateOptions = CreateOptions.Upsert, transaction: StreamTransaction = None.orNull): IO[CreateResult[T]] =
    insert(doc, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))

  def update(key: String, doc: T, options: UpdateOptions = UpdateOptions.Default, transaction: StreamTransaction = None.orNull): IO[UpdateResult[T]] = collection
    .updateDocument(key, fromT(doc), options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(updateDocumentEntityConversion(_, toT))

  def delete(key: String, options: DeleteOptions = DeleteOptions.Default, transaction: StreamTransaction = None.orNull): IO[DeleteResult[T]] = collection
    .deleteDocument(key, classOf[String], options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(deleteDocumentEntityConversion(_, toT))

  object batch {
    def insert(docs: List[T], options: CreateOptions = CreateOptions.Insert, transaction: StreamTransaction = None.orNull): IO[CreateResults[T]] = collection
      .insertDocuments(docs.map(fromT).asJava, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
      .toIO
      .map(multiDocumentCreateConversion(_, toT))

    def upsert(docs: List[T], options: CreateOptions = CreateOptions.Upsert, transaction: StreamTransaction = None.orNull): IO[CreateResults[T]] =
      insert(docs, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))

    def delete(docs: List[T], options: DeleteOptions = DeleteOptions.Default, transaction: StreamTransaction = None.orNull): IO[DeleteResults[T]] = collection
      .deleteDocuments(docs.map(fromT).asJava, classOf[String], options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
      .toIO
      .map(multiDocumentDeleteConversion(_, toT))
  }
}