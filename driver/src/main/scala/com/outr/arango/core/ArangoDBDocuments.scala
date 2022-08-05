package com.outr.arango.core

import cats.effect.IO
import com.arangodb.async.ArangoCollectionAsync
import com.outr.arango.Id
import com.outr.arango.util.Helpers._
import fabric.Json
import fabric.parse.{JsonParser, JsonWriter}

import scala.jdk.CollectionConverters._

trait ArangoDBDocuments[T] {
  protected def _collection: ArangoCollectionAsync
  final def stringToT(s: String): T = toT(JsonParser.parse(s))
  final def tToString(t: T): String = JsonParser.format(fromT(t), JsonWriter.Compact)

  def toT(value: Json): T
  def fromT(t: T): Json

  def id(key: String): Id[T] = Id[T](key, _collection.name())

  def apply(id: Id[T],
            default: Id[T] => T = id => throw NotFoundException(id._id)): IO[T] = get(id).map(_.getOrElse(default(id)))

  def get(id: Id[T]): IO[Option[T]] = _collection
    .getDocument(id._key, classOf[String])
    .toIO
    .map(s => Option(s).map(stringToT))

  def insert(doc: T, options: CreateOptions = CreateOptions.Insert, transaction: StreamTransaction = None.orNull): IO[CreateResult[T]] = _collection
    .insertDocument(tToString(doc), options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(createDocumentEntityConversion(_, stringToT))

  def upsert(doc: T, options: CreateOptions = CreateOptions.Upsert, transaction: StreamTransaction = None.orNull): IO[CreateResult[T]] =
    insert(doc, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))

  def update(id: Id[T], doc: T, options: UpdateOptions = UpdateOptions.Default, transaction: StreamTransaction = None.orNull): IO[UpdateResult[T]] = _collection
    .updateDocument(id._key, tToString(doc), options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(updateDocumentEntityConversion(_, stringToT))

  def delete(id: Id[T], options: DeleteOptions = DeleteOptions.Default, transaction: StreamTransaction = None.orNull): IO[DeleteResult[T]] = _collection
    .deleteDocument(id._key, classOf[String], options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
    .toIO
    .map(deleteDocumentEntityConversion(_, stringToT))

  object batch {
    def insert(docs: List[T], options: CreateOptions = CreateOptions.Insert, transaction: StreamTransaction = None.orNull): IO[CreateResults[T]] = _collection
      .insertDocuments(docs.map(tToString).asJava, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
      .toIO
      .map(multiDocumentCreateConversion(_, stringToT))

    def upsert(docs: List[T], options: CreateOptions = CreateOptions.Upsert, transaction: StreamTransaction = None.orNull): IO[CreateResults[T]] =
      insert(docs, options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))

    def delete(docs: List[T], options: DeleteOptions = DeleteOptions.Default, transaction: StreamTransaction = None.orNull): IO[DeleteResults[T]] = _collection
      .deleteDocuments(docs.map(tToString).asJava, classOf[String], options.copy(streamTransaction = options.streamTransaction.orElse(Option(transaction))))
      .toIO
      .map(multiDocumentDeleteConversion(_, stringToT))
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