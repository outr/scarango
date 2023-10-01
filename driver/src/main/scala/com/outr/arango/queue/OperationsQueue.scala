package com.outr.arango.queue

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.query._
import com.outr.arango.upsert.Searchable
import com.outr.arango.{Document, DocumentModel, DocumentRef}
import fabric.rw.RW

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

case class OperationsQueue[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M],
                                                                    flushSize: Int,
                                                                    chunkSize: Int) { oq =>
  private var queues = List.empty[ProcessQueue[D]]

  /**
    * Provide queue operations on a collection. Call `flush()` at the end to make sure all batched data is pushed.
    */
  object op {
    def create(process: List[D] => IO[Unit]): ProcessQueue[D] = {
      val q = ProcessQueue[D](
        process = process,
        flushSize = flushSize,
        chunkSize = chunkSize
      )
      oq.synchronized {
        queues = q :: queues
      }
      q
    }
    lazy val insert: ProcessQueue[D] = create(list => collection.batch.insert(list).void)
    lazy val upsert: ProcessQueue[D] = create(list => collection.batch.upsert(list).void)
    lazy val delete: ProcessQueue[D] = create(list => collection.batch.delete(list.map(_._id)).void)
    def createUpsertReplace(f: DocumentRef[D, M] => List[Searchable]): ProcessQueue[D] = create { list =>
      collection.upsert.withListSearch(list)(f).execute()
    }

    /**
      * Flushes the queue
      *
      * @param fullFlush if true, all operations are applied. If false, flushing only occurs until the operation count
      *                  is below the flushSize threshold.
      */
    def flush(fullFlush: Boolean = true): IO[Unit] = queues.map(_.flush(fullFlush)).sequence.void
  }
}