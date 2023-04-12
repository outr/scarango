package com.outr.arango.queue

import cats.effect.IO
import cats.syntax.all._
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.{Document, DocumentModel}

/**
  * Useful for batch operations where the batch may overflow. This will create multiple batches and flush as they fill
  * up. Call finish() at the end to make sure all unflushed batches get processed.
  *
  * @param batchSize the maximum number of records per batch (defaults to 1000)
  */
case class DBQueue(batchSize: Int = 1000,
                   map: Map[DocumentCollection[_, _], CollectionQueue[_, _]] = Map.empty) {
  def inserted: Int = map.map(_._2.inserted).sum
  def upserted: Int = map.map(_._2.upserted).sum
  def deleted: Int = map.map(_._2.deleted).sum

  private def collectionQueue[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M]): IO[CollectionQueue[D, M]] = IO {
    map.getOrElse(collection, CollectionQueue(batchSize, collection)).asInstanceOf[CollectionQueue[D, M]]
  }

  private def stream[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M],
                                       op: (CollectionQueue[D, M], D) => IO[CollectionQueue[D, M]],
                                       stream: fs2.Stream[IO, D]): IO[DBQueue] = stream
    .chunkN(batchSize)
    .evalScan(this)((queue, chunk) => queue.collectionQueue(collection).flatMap { queue =>
      chunk
        .toList
        .foldLeft(IO.pure(queue))((queue, doc) => queue.flatMap(q => op(q, doc)))
        .map { queue =>
          copy(map = map + (queue.collection -> queue))
        }
    })
    .compile
    .lastOrError

  def insert[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M], stream: fs2.Stream[IO, D]): IO[DBQueue] = {
    this.stream[D, M](
      collection = collection,
      op = (q, d) => q.withInsert(d),
      stream = stream
    )
  }

  def insert[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M], docs: D*): IO[DBQueue] =
    insert(collection, fs2.Stream[IO, D](docs: _*))

  def upsert[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M], stream: fs2.Stream[IO, D]): IO[DBQueue] =
    this.stream[D, M](
      collection = collection,
      op = (q, d) => q.withUpsert(d),
      stream = stream
    )

  def upsert[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M], docs: D*): IO[DBQueue] =
    upsert(collection, fs2.Stream[IO, D](docs: _*))

  def delete[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M], stream: fs2.Stream[IO, D]): IO[DBQueue] =
    this.stream[D, M](
      collection = collection,
      op = (q, d) => q.withDelete(d),
      stream = stream
    )

  def delete[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M], docs: D*): IO[DBQueue] =
    delete(collection, fs2.Stream[IO, D](docs: _*))

  def finish(): IO[DBQueue] = map.values.toList.map(_.finish()).sequence.map { cqs =>
    var m = map
    cqs.foreach { cq =>
      m += cq.collection -> cq
    }
    copy(map = m)
  }
}