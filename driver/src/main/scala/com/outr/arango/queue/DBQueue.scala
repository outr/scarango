package com.outr.arango.queue

import cats.effect.IO
import com.outr.arango.Document
import com.outr.arango.collection.DocumentCollection
import cats.syntax.all._

/**
  * Useful for batch operations where the batch may overflow. This will create multiple batches and flush as they fill
  * up. Call finish() at the end to make sure all unflushed batches get processed.
  *
  * @param batchSize the maximum number of records per batch (defaults to 1000)
  */
case class DBQueue(batchSize: Int = 1000,
                   map: Map[DocumentCollection[_], CollectionQueue[_]] = Map.empty,
                   inserts: Int = 0,
                   upserts: Int = 0,
                   deletes: Int = 0) {
  private def collectionQueue[D <: Document[D]](collection: DocumentCollection[D]): IO[CollectionQueue[D]] = IO {
    map.getOrElse(collection, CollectionQueue(batchSize, collection)).asInstanceOf[CollectionQueue[D]]
  }

  private def withQueue[D <: Document[D]](collection: DocumentCollection[D],
                                          op: (CollectionQueue[D], D) => IO[CollectionQueue[D]],
                                          inc: DBQueue => DBQueue,
                                          docs: D*): IO[DBQueue] = {
    collectionQueue(collection).flatMap { queue =>
      docs
        .foldLeft(IO.pure(queue))((queue, doc) => queue.flatMap(q => op(q, doc)))
        .map { queue =>
          inc(copy(map = map + (collection -> queue)))
        }
    }
  }

  def insert[D <: Document[D]](collection: DocumentCollection[D], docs: D*): IO[DBQueue] =
    withQueue[D](collection, (q, d) => q.withInsert(d), _.copy(inserts = inserts + docs.length), docs: _*)

  def upsert[D <: Document[D]](collection: DocumentCollection[D], docs: D*): IO[DBQueue] =
    withQueue[D](collection, (q, d) => q.withUpsert(d), _.copy(upserts = upserts + docs.length), docs: _*)

  def delete[D <: Document[D]](collection: DocumentCollection[D], docs: D*): IO[DBQueue] =
    withQueue[D](collection, (q, d) => q.withDelete(d), _.copy(deletes = deletes + docs.length), docs: _*)

  def finish(): IO[Unit] = map.values.toList.map(_.finish()).sequence.map(_ => ())
}