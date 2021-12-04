package com.outr.arango

import com.outr.arango.api.{APIWalTail, WALOperations}
import io.youi.client.HttpClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ArangoWriteAheadLog(client: HttpClient) {
  def tail(global: Boolean = false,
           from: Option[Long] = None,
           to: Option[Long] = None,
           lastScanned: Long = 0L,
           chunkSize: Option[Long] = None,
           syncerId: Option[Long] = None,
           serverId: Option[Long] = None,
           clientId: String = "scarango")
          (implicit ec: ExecutionContext): Future[WALOperations] = {
    APIWalTail.get(
      client = client,
      global = Some(global),
      from = from,
      to = to,
      lastScanned = lastScanned,
      chunkSize = chunkSize,
      syncerId = syncerId,
      serverId = serverId,
      clientId = Some(clientId)
    )
  }

  def monitor(global: Boolean = false,
              from: Option[Long] = None,
              to: Option[Long] = None,
              lastScanned: Long = 0L,
              chunkSize: Option[Long] = None,
              syncerId: Option[Long] = None,
              serverId: Option[Long] = None,
              clientId: String = "scarango",
              delay: FiniteDuration = 5.seconds,
              skipHistory: Boolean = true,
              failureHandler: Throwable => Option[FiniteDuration] = t => {
                scribe.error("Monitor error", t)
                None
              })
             (implicit ec: ExecutionContext): WriteAheadLogMonitor = {
    val m = new WriteAheadLogMonitor(delay, skipHistory, failureHandler)
    m.run(tail(global, from, to, lastScanned, chunkSize, syncerId, serverId, clientId))
    m
  }
}