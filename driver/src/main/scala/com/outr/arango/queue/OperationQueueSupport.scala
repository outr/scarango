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
  * you are needing to do various operations across potentially multiple collections efficiently. Make sure to call
  * `flushQueue()` when finished to avoid un-pushed operations.
  */
trait OperationQueueSupport {
  protected def opFlushSize: Int = 10_000
  protected def opChunkSize: Int = 1_000

  private val ops = TrieMap.empty[String, OperationsQueue[_, _]]

  implicit def collectionToOps[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M]): OperationsQueue[D, M] = {
    val q = ops.getOrElseUpdate(collection.name, OperationsQueue(collection, opFlushSize, opChunkSize))
    q.asInstanceOf[OperationsQueue[D, M]]
  }

  /**
    * Fully flushes all pending operation queues.
    */
  def flushQueue(): IO[Unit] = ops.values.map(_.op.flush()).toList.sequence.void

  /**
    * Clears the operation queues and removes all pending operations.
    */
  def clearQueue(): IO[Unit] = IO(ops.clear())
}