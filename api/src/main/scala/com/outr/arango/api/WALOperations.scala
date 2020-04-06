package com.outr.arango.api

import io.youi.client.HttpClient

import scala.concurrent.{ExecutionContext, Future}

case class WALOperations(private val client: HttpClient,
                         global: Option[Boolean],
                         chunkSize: Option[Long],
                         syncerId: Option[Long],
                         serverId: Option[Long],
                         clientId: Option[String],
                         checkMore: Boolean,
                         fromPresent: Boolean,
                         lastIncluded: Long,
                         lastScanned: Long,
                         lastTick: Long,
                         operations: List[WALOperation]) {
  def tail(from: Long = lastIncluded)(implicit ec: ExecutionContext): Future[WALOperations] = APIWalTail.get(
    client = client,
    global = global,
    from = Some(from),
    to = None,
    lastScanned = lastScanned,
    chunkSize = chunkSize,
    syncerId = syncerId,
    serverId = serverId,
    clientId = clientId
  )
}