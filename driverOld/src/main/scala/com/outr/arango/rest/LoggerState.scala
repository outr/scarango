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

case class LogEvent(tick: String,
                    `type`: Int,
                    tid: Option[String],
                    database: String,
                    cid: Option[String],
                    cname: Option[String],
                    data: Option[Json]) {
  // TODO: switch back after https://github.com/arangodb/arangodb/issues/2868 is addressed
//  def collection: String = cname.getOrElse(throw new RuntimeException(s"No collection defined for $eventType."))
  lazy val collectionOption: Option[String] = cname.orElse {
    data.flatMap(json => (json \\ "_id").headOption.map(_.asString.get).map(s => s.substring(0, s.indexOf('/'))))
  }
  def collection: String = collectionOption.getOrElse(throw new RuntimeException(s"No collection defined for $eventType."))
  lazy val eventType: EventType = EventType(`type`)
}

sealed abstract class EventType(val value: Int)

object EventType {
  case object LoggerStopped extends EventType(1000)
  case object LoggerStarted extends EventType(1001)

  case object CollectionCreated extends EventType(2000)
  case object CollectionDropped extends EventType(2001)
  case object CollectionRenamed extends EventType(2002)
  case object CollectionPropChanged extends EventType(2003)

  case object IndexCreated extends EventType(2100)
  case object IndexDropped extends EventType(2101)

  case object TransactionStarted extends EventType(2200)
  case object TransactionCommitted extends EventType(2201)

  case object DocumentUpsert extends EventType(2300)
  case object EdgeUpsert extends EventType(2301)
  case object Deletion extends EventType(2302)

  private val items = List(
    LoggerStopped,
    LoggerStarted,
    CollectionCreated,
    CollectionDropped,
    CollectionRenamed,
    CollectionPropChanged,
    IndexCreated,
    IndexDropped,
    TransactionStarted,
    TransactionCommitted,
    DocumentUpsert,
    EdgeUpsert,
    Deletion
  )

  private val map = items.map(et => et.value -> et).toMap

  def get(value: Int): Option[EventType] = map.get(value)
  def apply(value: Int): EventType = map(value)
}