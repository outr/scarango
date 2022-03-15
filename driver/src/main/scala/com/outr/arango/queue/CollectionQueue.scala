package com.outr.arango.queue

import cats.effect.IO
import com.outr.arango.Document
import com.outr.arango.collection.DocumentCollection

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