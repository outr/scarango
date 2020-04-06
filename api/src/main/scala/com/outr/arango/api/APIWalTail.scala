package com.outr.arango.api

import io.circe.Decoder.Result
import io.youi.client.HttpClient
import io.youi.http.{HeaderKey, HttpMethod}
import io.youi.net._
import io.circe.{Decoder, DecodingFailure, HCursor}
import profig.JsonUtil

import scala.concurrent.{ExecutionContext, Future}
      
object APIWalTail {
  private implicit def operationTypeDecoder: Decoder[OperationType] = new Decoder[OperationType] {
    override def apply(c: HCursor): Result[OperationType] = c.value.asNumber match {
      case Some(n) => Right(OperationType(n.toInt.get))
      case None => Left(DecodingFailure(s"OperationType not a number: ${c.value}", Nil))
    }
  }

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
        val operations = lines.map { line =>
          try {
            JsonUtil.fromJsonString[WALOperation](line)
          } catch {
            case t: Throwable => throw new RuntimeException(s"Parsing failure for: $line", t)
          }
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