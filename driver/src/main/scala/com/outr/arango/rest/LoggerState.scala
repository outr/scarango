package com.outr.arango.rest

import io.circe.Json

case class LoggerState(state: CurrentState, server: ServerInfo, clients: List[ConnectedClient])

case class CurrentState(running: Boolean,
                        lastLogTick: Long,
                        lastUncommittedLogTick: Long,
                        totalEvents: Long,
                        time: String)

case class ServerInfo(version: String, serverId: String)

case class ConnectedClient(serverId: String, lastServedTick: String, time: String)

case class LoggerFollow(active: Boolean, lastIncluded: Long, lastTick: Long, checkMore: Boolean, events: List[LogEvent])

case class LogEvent(tick: String, `type`: Int, tid: String, database: String, cid: String, cname: String, data: Json)