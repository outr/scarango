package com.outr.arango.queue

import cats.effect.IO

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
  * ProcessQueue provides a convenience capability to batch process in chunks.
  *
  * @param process   the function to process a chunk of the queue
  * @param flushSize the number of records before a flush occurs
  * @param chunkSize the max number of records per chunk sent to the process function
  */
case class ProcessQueue[T](process: List[T] => IO[Unit], flushSize: Int, chunkSize: Int) {
  private lazy val queue = new ConcurrentLinkedQueue[T]
  private lazy val counter = new AtomicInteger(0)

  private lazy val _processed = new AtomicInteger(0)

  def processed: Int = _processed.get()

  private def take(n: Int): List[T] = if (n == 0) {
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

  /**
    * Queue operations for the supplied docs. If this causes the flushSize to overflow, a flush will occur before this
    * returns. Otherwise, this is a very fast operation.
    */
  def apply(docs: T*): IO[Unit] = IO {
    docs.foreach(queue.add)
    counter.addAndGet(docs.length)
  }.flatMap { size =>
    if (size >= flushSize) {
      flush(fullFlush = false)
    } else {
      IO.unit
    }
  }

  /**
    * Flushes the queue
    *
    * @param fullFlush if true, all operations are applied. If false, flushing only occurs until the operation count
    *                  is below the flushSize threshold.
    */
  def flush(fullFlush: Boolean = true): IO[Unit] = IO(take(chunkSize)).flatMap { list =>
    if (list.isEmpty) {
      IO.unit
    } else {
      process(list).flatMap { _ =>
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
