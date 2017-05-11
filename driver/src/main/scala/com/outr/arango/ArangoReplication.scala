package com.outr.arango

import com.outr.arango.rest.{LogEvent, LoggerFollow, LoggerState}
import io.youi.http.{FileContent, HeaderKey, Method, StringContent}
import io.circe.generic.auto._
import io.circe.parser._
import org.powerscala.io._

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
    val path = s"/_db/${db.db}/_api/replication/logger-follow"
    db.session.instance.send(path, token = db.session.token, params = params).map { response =>
      val contentString = response.content.get match {
        case content: StringContent => content.value
        case content: FileContent => IO.stream(content.file, new StringBuilder).toString
        case content => throw new RuntimeException(s"Unsupported content $content")
      }
      val active = response.headers.first(HeaderKey("x-arango-replication-active")).get.toBoolean
      val lastIncluded = response.headers.first(HeaderKey("x-arango-replication-lastincluded")).get.toLong
      val lastTick = response.headers.first(HeaderKey("x-arango-replication-lasttick")).get.toLong
      val checkMore = response.headers.first(HeaderKey("x-arango-replication-checkmore")).get.toBoolean
      val json = contentString.split('\n').mkString("[", ", ", "]")
      decode[List[LogEvent]](json) match {
        case Left(error) => throw new RuntimeException(s"JSON decoding error: $contentString", error)
        case Right(entries) => LoggerFollow(active, lastIncluded, lastTick, checkMore, entries)
      }
    }
  }
}