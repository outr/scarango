package com.outr.arango

import reactify.Channel

import scala.concurrent.Future

class CollectionMonitor[D <: Document[D]](monitor: WriteAheadLogMonitor,
                                          collection: Collection[D],
                                          val started: Future[Unit]) extends Channel[Operation[D]] {
  monitor.attach { wop =>
    val op = Operation[D](wop, collection.graph)
    if (op.underlying.collectionId.contains(collection.id) || op.collectionName.contains(collection.name)) {
      static(op)
    }
  }
}