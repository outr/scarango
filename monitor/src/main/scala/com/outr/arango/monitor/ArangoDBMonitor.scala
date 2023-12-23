package com.outr.arango.monitor

import cats.effect.{ExitCode, IO, IOApp}
import com.outr.arango.Graph
import com.outr.arango.core.ArangoDBConfig
import fabric.Json
import fabric.rw._
import fabric.io.JsonFormatter
import spice.http.client.HttpClient
import spice.net._

// TODO: Work in progress
case class ArangoDBMonitor(config: ArangoDBConfig = ArangoDBConfig()) {
  private lazy val client = HttpClient
    .url(URL(
      protocol = if (config.ssl) Protocol.Https else Protocol.Http,
      host = config.hosts.head.host,
      port = config.hosts.head.port
    ))
  // http://localhost:8529/_api/wal/tail?from=188771
  // TODO: Handle user authentication
  def tail(db: Option[String] = None,
           from: Option[Int] = None,
           to: Option[Int] = None,
           syncerId: Option[String] = None): IO[Unit] = {
    val path = db match {
      case Some(dbName) => s"/_db/$dbName/_api/wal/tail"
      case None => "/_api/wal/tail"
    }
    client
      .path(URLPath.parse(path))
      .call[Json]
      .map { json =>
        scribe.info(s"Tail: ${JsonFormatter.Default(json)}")
      }
  }
}

object ArangoDBMonitor extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val monitor = ArangoDBMonitor()
    monitor.tail().map(_ => ExitCode.Success)
  }
}