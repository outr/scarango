package com.outr.arango

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait MonitoredSupport {
  this: Graph =>

  protected def monitorFrequency: FiniteDuration = 250.millis
  protected def monitorExecutionContext: ExecutionContext = scribe.Execution.global

  lazy val monitor: WriteAheadLogMonitor = wal.monitor(delay = monitorFrequency)(monitorExecutionContext)
  private var monitorMap = Map.empty[String, CollectionMonitor[_]]

  def collectionMonitor[D <: Document[D]](collection: Collection[D]): CollectionMonitor[D] = monitor.synchronized {
    monitorMap.get(collection.name) match {
      case Some(cm) => cm.asInstanceOf[CollectionMonitor[D]]
      case None => {
        val cm = collection.monitor(monitor)
        monitorMap += collection.name -> cm
        cm
      }
    }
  }
}