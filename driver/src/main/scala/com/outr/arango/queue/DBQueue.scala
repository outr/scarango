package com.outr.arango.queue

import cats.effect.IO
import com.outr.arango.Document
import com.outr.arango.collection.DocumentCollection
import cats.syntax.all._

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

case class CollectionQueue[D <: Document[D]](batchSize: Int,
                                             collection: DocumentCollection[D],
                                             insert: Vector[D] = Vector.empty[D],
                                             upsert: Vector[D] = Vector.empty[D],
                                             delete: Vector[D] = Vector.empty[D]) {
  def withInsert(doc: D): IO[CollectionQueue[D]] = {
    val updated = insert :+ doc
    if (updated.length >= batchSize) {
      collection.batch.insert(updated.toList).map(_ => copy(insert = Vector.empty))
    } else {
      IO.pure(copy(insert = updated))
    }
  }
  def withUpsert(doc: D): IO[CollectionQueue[D]] = {
    val updated = upsert :+ doc
    if (updated.length >= batchSize) {
      collection.batch.upsert(updated.toList).map(_ => copy(upsert = Vector.empty))
    } else {
      IO.pure(copy(upsert = updated))
    }
  }
  def withDelete(doc: D): IO[CollectionQueue[D]] = {
    val updated = delete :+ doc
    if (updated.length >= batchSize) {
      collection.batch.delete(updated.toList).map(_ => copy(delete = Vector.empty))
    } else {
      IO.pure(copy(delete = updated))
    }
  }

  def finish(): IO[Unit] = for {
    _ <- if (insert.nonEmpty) collection.batch.insert(insert.toList) else IO.unit
    _ <- if (upsert.nonEmpty) collection.batch.upsert(upsert.toList) else IO.unit
    _ <- if (delete.nonEmpty) collection.batch.delete(delete.toList) else IO.unit
  } yield {
    ()
  }
}