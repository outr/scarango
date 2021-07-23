package com.outr.arango.api

import fabric.parse.Json
import fabric.rw.Asable
import io.youi.client.HttpClient
import io.youi.http.{HeaderKey, HttpMethod}
import io.youi.net._

import scala.concurrent.{ExecutionContext, Future}
      
object APIWalTail {
  def get(client: HttpClient,
          global: Option[Boolean] = None,
          from: Option[Long] = None,
          to: Option[Long] = None,
          lastScanned: Long = 0L,
          chunkSize: Option[Long] = None,
          syncerId: Option[Long] = None,
          serverId: Option[Long] = None,
          clientId: Option[String] = None)(implicit ec: ExecutionContext): Future[WALOperations] = {
    client
      .method(HttpMethod.Get)
      .path(path"/_api/wal/tail", append = true)
      .param[Option[Long]]("from", from, None)
      .param[Option[Long]]("to", to, None)
      .param[Long]("lastScanned", lastScanned, 0L)
      .param[Option[Boolean]]("global", global, None)
      .param[Option[Long]]("chunkSize", chunkSize, None)
      .param[Option[Long]]("syncerId", syncerId, None)
      .param[Option[Long]]("serverId", serverId, None)
      .param[Option[String]]("clientId", clientId, None)
      .send()
      .map { response =>
        val lines = response.content.map(_.asString).getOrElse("").split('\n').toList
        val operations = lines.map(_.trim).collect {
          case line if line.nonEmpty => Json.parse(line).as[WALOperation]
        }
        val headers = response.headers
        WALOperations(
          client = client,
          global = global,
          chunkSize = chunkSize,
          syncerId = syncerId,
          serverId = serverId,
          clientId = clientId,
          checkMore = headers.first(HeaderKey("X-Arango-Replication-Checkmore")).exists(_.toBoolean),
          fromPresent = headers.first(HeaderKey("X-Arango-Replication-Frompresent")).exists(_.toBoolean),
          lastIncluded = headers.first(HeaderKey("X-Arango-Replication-Lastincluded")).map(_.toLong).getOrElse(-1L),
          lastScanned = headers.first(HeaderKey("X-Arango-Replication-Lastscanned")).map(_.toLong).getOrElse(-1L),
          lastTick = headers.first(HeaderKey("X-Arango-Replication-Lasttick")).map(_.toLong).getOrElse(-1L),
          operations = operations
        )
      }
  }
}