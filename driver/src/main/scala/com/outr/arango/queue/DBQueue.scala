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

  def insert[D <: Document[D]](docAndCollection: (D, DocumentCollection[D])): IO[DBQueue] = {
    val collection = docAndCollection._2
    collectionQueue(collection).flatMap { queue =>
      queue.withInsert(docAndCollection._1).map { cq =>
        copy(map = map + (collection -> cq), inserts = inserts + 1)
      }
    }
  }

  def upsert[D <: Document[D]](docAndCollection: (D, DocumentCollection[D])): IO[DBQueue] = {
    val collection = docAndCollection._2
    collectionQueue(collection).flatMap { queue =>
      queue.withUpsert(docAndCollection._1).map { cq =>
        copy(map = map + (collection -> cq), upserts = upserts + 1)
      }
    }
  }

  def delete[D <: Document[D]](docAndCollection: (D, DocumentCollection[D])): IO[DBQueue] = {
    val collection = docAndCollection._2
    collectionQueue(collection).flatMap { queue =>
      queue.withDelete(docAndCollection._1).map { cq =>
        copy(map = map + (collection -> cq), deletes = deletes + 1)
      }
    }
  }

  def finish(): IO[Unit] = map.values.toList.map(_.finish()).sequence.map(_ => ())
}