package com.outr.arango.managed

import akka.actor.{ActorSystem, Cancellable, Terminated}
import com.outr.arango.ReplicationResult
import com.outr.arango.rest.LogEvent
import reactify.Observable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class RealTime(graph: Graph) {
  private lazy val system = ActorSystem("GraphRealTime")
  private var cancellable: Option[Cancellable] = None

  lazy val events: Observable[LogEvent] = graph.monitor

  def start(delay: FiniteDuration = 500.millis): Unit = synchronized {
    assert(cancellable.isEmpty, "Graph.realTime is already started.")
    cancellable = Some(system.scheduler.schedule(delay, delay) {
      update()
    })
  }

  def update(): ReplicationResult = Try(graph.monitor.updateAndWait()) match {
    case Success(result) => result
    case Failure(e) => {
      scribe.error("Error occurred while executing RealTime polling")
      scribe.error(e)
      ReplicationResult.NoResults
    }
  }

  def stop(): Unit = {
    cancellable.foreach(_.cancel())
    cancellable = None
  }

  def started: Boolean = cancellable.nonEmpty

  def shutdown(): Future[Terminated] = synchronized {
    stop()
    system.terminate()
  }
}