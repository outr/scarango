package com.outr.arango.queue

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.outr.arango.{Document, DocumentModel}
import com.outr.arango.collection.DocumentCollection

import scala.collection.concurrent.TrieMap
import scala.language.implicitConversions

/**
  * Mix-in to provide operation queueing capabilities for DocumentModel. With this mixed in, you can use implicits to
  * call:
  *
  * database.collection.op.upsert(docs: _*): IO[Unit]
  *
  * This will queue up to `opFlushSize` and then stream the batch in `opChunkSize` into the database. Very useful when
  * you are needing to do various operations across potentially multiple collections efficiently.
  */
trait OperationQueueSupport {
  protected def opFlushSize: Int = 10_000
  protected def opChunkSize: Int = 1_000

  private val ops = TrieMap.empty[String, OperationsQueue[_, _]]

  implicit def collectionToOps[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M]): OperationsQueue[D, M] = {
    val q = ops.getOrElseUpdate(collection.dbName, OperationsQueue(collection, opFlushSize, opChunkSize))
    q.asInstanceOf[OperationsQueue[D, M]]
  }

  def flushQueue(): IO[Unit] = ops.values.map(_.flush()).toList.sequence.void

  def clear(): IO[Unit] = IO(ops.clear())
}