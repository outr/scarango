package com.outr.arango

import java.util.concurrent.atomic.{AtomicBoolean, AtomicLong}

import com.outr.arango.rest.{LogEvent, LoggerFollow, LoggerState}
import io.youi.http.{FileContent, HeaderKey, Method, StringContent}
import io.circe.generic.auto._
import io.circe.parser._
import org.powerscala.io._
import reactify.{InvocationType, Observable}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ArangoReplication(db: ArangoDB) {
  def state(): Future[LoggerState] = db.call[LoggerState]("replication/logger-state", Method.Get)

  def follow(from: Option[Long] = None,
             to: Option[Long] = None,
             chunkSize: Option[Long] = None,
             includeSystem: Boolean = true): Future[LoggerFollow] = {
    val params = List(
      from.map("from" -> _.toString),
      to.map("to" -> _.toString),
      chunkSize.map("chunkSize" -> _.toString),
      Some("includeSystem" -> includeSystem.toString)
    ).flatten.toMap
    db.session.send(Some(db.db), "replication/logger-follow", params = params).map { response =>
      val contentStringOption = response.content.map {
        case content: StringContent => content.value
        case content: FileContent => IO.stream(content.file, new StringBuilder).toString
        case content => throw new RuntimeException(s"Unsupported content $content")
      }
      val active = response.headers.first(HeaderKey("x-arango-replication-active")).get.toBoolean
      val lastIncluded = response.headers.first(HeaderKey("x-arango-replication-lastincluded")).get.toLong
      val lastTick = response.headers.first(HeaderKey("x-arango-replication-lasttick")).get.toLong
      val checkMore = response.headers.first(HeaderKey("x-arango-replication-checkmore")).get.toBoolean
      val logEvents = contentStringOption match {
        case Some(contentString) => {
          val json = contentString.split('\n').mkString("[", ", ", "]")
          decode[List[LogEvent]](json) match {
            case Left(error) => throw new RuntimeException(s"JSON decoding error: $contentString", error)
            case Right(entries) => entries
          }
        }
        case None => Nil
      }
      LoggerFollow(active, lastIncluded, lastTick, checkMore, logEvents)
    }
  }

  /**
    * ReplicationMonitor is an Observable that simplifies the process of monitoring the replication state. Periodic
    * calls to the `update` method will check for and fire `LogEvents`.
    *
    * Note: You *must* call `update` or no events will ever fire.
    */
  lazy val monitor: ReplicationMonitor = new ReplicationMonitor(this)
}

class ReplicationMonitor(replication: ArangoReplication) extends Observable[LogEvent] {
  private val running = new AtomicBoolean(false)
  private val lastTick = new AtomicLong(0L)

  def update(): Future[Unit] = {
    if (lastTick.get() == 0L) {
      replication.state().map { state =>
        lastTick.set(state.state.lastLogTick)
      }
    } else {
      replication.follow(from = Some(lastTick.get())).map { follow =>
        follow.events.foreach(fire(_, InvocationType.Direct))
        lastTick.set(follow.lastIncluded)
      }
    }
  }

  def updateAndWait(): Unit = Arango.synchronous(update())
}