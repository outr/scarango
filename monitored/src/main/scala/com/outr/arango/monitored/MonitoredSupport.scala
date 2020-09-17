package com.outr.arango.monitored

import com.outr.arango._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

trait MonitoredSupport {
  this: Graph =>

  protected def monitorFrequency: FiniteDuration = 250.millis
  protected def monitorExecutionContext: ExecutionContext = scribe.Execution.global

  object monitor extends WriteAheadLogMonitor(monitorFrequency) {
    def frequency: FiniteDuration = monitorFrequency
    def ec: ExecutionContext = monitorExecutionContext

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

  def materialized[Base <: Document[Base], Into <: Document[Into]](baseInto: (Collection[Base], WritableCollection[Into])): MaterializedBuilder[Base, Into] = {
    val (base, into) = baseInto
    MaterializedBuilder(this, base, into, Nil)
  }
}