package com.outr.arango.queue

import cats.effect.IO
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.{Document, DocumentModel}

case class CollectionQueue[D <: Document[D], M <: DocumentModel[D]](batchSize: Int,
                                             collection: DocumentCollection[D, M],
                                             insert: Vector[D] = Vector.empty[D],
                                             upsert: Vector[D] = Vector.empty[D],
                                             delete: Vector[D] = Vector.empty[D],
                                             inserted: Int = 0,
                                             upserted: Int = 0,
                                             deleted: Int = 0) {
  def withInsert(doc: D): IO[CollectionQueue[D, M]] = {
    val updated = copy(insert = insert :+ doc)
    if (updated.insert.length >= batchSize) {
      updated.flushInsert()
    } else {
      IO.pure(updated)
    }
  }

  def withUpsert(doc: D): IO[CollectionQueue[D, M]] = {
    val updated = copy(upsert = upsert :+ doc)
    if (updated.upsert.length >= batchSize) {
      updated.flushUpsert()
    } else {
      IO.pure(updated)
    }
  }

  def withDelete(doc: D): IO[CollectionQueue[D, M]] = {
    val updated = copy(delete = delete :+ doc)
    if (updated.delete.length >= batchSize) {
      updated.flushDelete()
    } else {
      IO.pure(updated)
    }
  }

  def flushInsert(): IO[CollectionQueue[D, M]] = if (insert.nonEmpty) {
    collection.batch
      .insert(insert.toList)
      .map(_ => copy(insert = Vector.empty, inserted = inserted + insert.length))
  } else {
    IO.pure(this)
  }

  def flushUpsert(): IO[CollectionQueue[D, M]] = if (upsert.nonEmpty) {
    collection.batch
      .upsert(upsert.toList)
      .map(_ => copy(upsert = Vector.empty, upserted = upserted + upsert.length))
  } else {
    IO.pure(this)
  }

  def flushDelete(): IO[CollectionQueue[D, M]] = if (delete.nonEmpty) {
    collection.batch
      .delete(delete.toList.map(_._id))
      .map(_ => copy(delete = Vector.empty, deleted = deleted + delete.length))
  } else {
    IO.pure(this)
  }

  def finish(): IO[CollectionQueue[D, M]] = for {
    a <- flushInsert()
    b <- a.flushUpsert()
    c <- b.flushDelete()
  } yield {
    c
  }
}