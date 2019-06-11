package com.outr.arango

import scala.concurrent.Await
import scala.concurrent.duration._

class QueryResponseIterator[T](private var pagination: QueryResponsePagination[T],
                               timeout: FiniteDuration = 10.seconds) extends Iterator[T] {
  private var items = pagination.results

  override def hasNext: Boolean = synchronized {
    items.nonEmpty || pagination.hasNext
  }
  override def next(): T = synchronized {
    items.headOption match {
      case Some(i) => {
        items = items.tail
        i
      }
      case None => {
        pagination = Await.result(pagination.next(), timeout)
        items = pagination.results
        next()
      }
    }
  }
}
