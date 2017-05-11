package com.outr.arango.managed

import akka.actor.{ActorSystem, Cancellable, Terminated}
import com.outr.arango.rest.LogEvent
import reactify.Observable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class RealTime(graph: Graph) {
  private lazy val system = ActorSystem("Graph.realTime")
  private var cancellable: Option[Cancellable] = None

  lazy val events: Observable[LogEvent] = graph.monitor

  def start(delay: FiniteDuration = 250.millis): Unit = synchronized {
    assert(cancellable.isEmpty, "Graph.realTime is already started.")
    cancellable = Some(system.scheduler.schedule(delay, delay) {
      graph.monitor.update()
    })
  }

  def shutdown(): Future[Terminated] = system.terminate()
}