package com.outr.arango

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait MonitoredSupport {
  this: Graph =>

  protected def monitorFrequency: FiniteDuration = 250.millis
  protected def monitorExecutionContext: ExecutionContext = scribe.Execution.global

  object monitor extends WriteAheadLogMonitor(monitorFrequency) {
    private var map = Map.empty[String, CollectionMonitor[_]]

    run(wal.tail()(monitorExecutionContext))(monitorExecutionContext)

    def apply[D <: Document[D]](collection: Collection[D]): CollectionMonitor[D] = synchronized {
      map.get(collection.name) match {
        case Some(cm) => cm.asInstanceOf[CollectionMonitor[D]]
        case None => {
          val cm = collection.monitor(monitor)
          map += collection.name -> cm
          cm
        }
      }
    }
  }
}