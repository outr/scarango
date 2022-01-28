package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.util.Helpers._

import scala.jdk.CollectionConverters._

trait ArangoDBDocuments[T] {
  protected def _collection: ArangoCollectionAsync
  protected def toT(s: String): T
  protected def fromT(t: T): String

  def apply(key: String,
            default: String => T = key => throw NotFoundException(key)): IO[T] = get(key).map(_.getOrElse(default(key)))

  def get(key: String): IO[Option[T]] = _collection
    .getDocument(key, classOf[String])
    .toIO
    .map(s => Option(s).map(toT))

  def insert(doc: T, options: CreateOptions = CreateOptions.Insert, transaction: StreamTransaction = None.orNull): IO[CreateResult[T]] = _collection
    .insertDocument(fromT(doc), options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(createDocumentEntityConversion(_, toT))

  def upsert(doc: T, options: CreateOptions = CreateOptions.Upsert, transaction: StreamTransaction = None.orNull): IO[CreateResult[T]] =
    insert(doc, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))

  def update(key: String, doc: T, options: UpdateOptions = UpdateOptions.Default, transaction: StreamTransaction = None.orNull): IO[UpdateResult[T]] = _collection
    .updateDocument(key, fromT(doc), options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(updateDocumentEntityConversion(_, toT))

  def delete(key: String, options: DeleteOptions = DeleteOptions.Default, transaction: StreamTransaction = None.orNull): IO[DeleteResult[T]] = _collection
    .deleteDocument(key, classOf[String], options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(deleteDocumentEntityConversion(_, toT))

  object batch {
    def insert(docs: List[T], options: CreateOptions = CreateOptions.Insert, transaction: StreamTransaction = None.orNull): IO[CreateResults[T]] = _collection
      .insertDocuments(docs.map(fromT).asJava, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
      .toIO
      .map(multiDocumentCreateConversion(_, toT))

    def upsert(docs: List[T], options: CreateOptions = CreateOptions.Upsert, transaction: StreamTransaction = None.orNull): IO[CreateResults[T]] =
      insert(docs, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))

    def delete(docs: List[T], options: DeleteOptions = DeleteOptions.Default, transaction: StreamTransaction = None.orNull): IO[DeleteResults[T]] = _collection
      .deleteDocuments(docs.map(fromT).asJava, classOf[String], options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
      .toIO
      .map(multiDocumentDeleteConversion(_, toT))
  }

  object stream {
    def apply(docs: fs2.Stream[IO, T], chunkSize: Int, process: fs2.Chunk[T] => IO[Int]): IO[Int] = docs
      .chunkN(chunkSize)
      .evalMap(process)
      .compile
      .foldMonoid

    def insert(docs: fs2.Stream[IO, T],
               chunkSize: Int = 1000,
               options: CreateOptions = CreateOptions.Insert,
               transaction: StreamTransaction = None.orNull): IO[Int] =
      apply(docs, chunkSize, chunk => batch.insert(chunk.toList, options, transaction).as(chunk.size))

    def upsert(docs: fs2.Stream[IO, T],
               chunkSize: Int = 1000,
               options: CreateOptions = CreateOptions.Upsert,
               transaction: StreamTransaction = None.orNull): IO[Int] =
      apply(docs, chunkSize, chunk => batch.upsert(chunk.toList, options, transaction).as(chunk.size))

    def delete(docs: fs2.Stream[IO, T],
               chunkSize: Int = 1000,
               options: DeleteOptions = DeleteOptions.Default,
               transaction: StreamTransaction = None.orNull): IO[Int] =
      apply(docs, chunkSize, chunk => batch.delete(chunk.toList, options, transaction).as(chunk.size))
  }
}