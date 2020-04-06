package com.outr.arango

import reactify.Channel

class CollectionMonitor[D <: Document[D]](monitor: WriteAheadLogMonitor, collection: Collection[D]) extends Channel[Operation[D]] {
  monitor.attach { wop =>
    val op = Operation[D](wop, collection.graph)
    if (op.collectionName.contains(collection.name)) {
      static(op)
    }
  }
}
