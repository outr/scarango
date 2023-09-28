package com.outr.arango.queue

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.outr.arango.collection.DocumentCollection
import com.outr.arango.{Document, DocumentModel}

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

case class OperationsQueue[D <: Document[D], M <: DocumentModel[D]](collection: DocumentCollection[D, M],
                                                                    flushSize: Int,
                                                                    chunkSize: Int) {
  oq =>
  private var queues = List.empty[OpQueue]

  object op {
    lazy val insert: OpQueue = OpQueue(stream => collection.stream.insert(stream, chunkSize).void)
    lazy val upsert: OpQueue = OpQueue(stream => collection.stream.upsert(stream, chunkSize).void)
    lazy val delete: OpQueue = OpQueue(stream => collection.stream.delete(stream.map(_._id), chunkSize).void)
  }

  def flush(): IO[Unit] = queues.map(_.flush()).sequence.void

  case class OpQueue(process: fs2.Stream[IO, D] => IO[Unit]) {
    oq.synchronized {
      queues = this :: queues
    }

    private lazy val queue = new ConcurrentLinkedQueue[D]
    private lazy val counter = new AtomicInteger(0)

    private lazy val _processed = new AtomicInteger(0)

    def processed: Int = _processed.get()

    private def take(n: Int): List[D] = if (n == 0) {
      Nil
    } else {
      val d = queue.poll()
      if (d == null) {
        Nil
      } else {
        counter.decrementAndGet()
        d :: take(n - 1)
      }
    }

    def apply(docs: D*): IO[Unit] = IO {
      docs.foreach(queue.add)
      counter.addAndGet(docs.length)
    }.flatMap { size =>
      if (size >= flushSize) {
        flush()
      } else {
        IO.unit
      }
    }

    def flush(fullFlush: Boolean = false): IO[Unit] = IO(take(chunkSize)).flatMap { list =>
      if (list.isEmpty) {
        IO.unit
      } else {
        val stream = fs2.Stream(list: _*).covary[IO]
        process(stream).flatMap { _ =>
          _processed.addAndGet(list.length)
          if (counter.get() >= flushSize || fullFlush) {
            flush()
          } else {
            IO.unit
          }
        }
      }
    }
  }
}
