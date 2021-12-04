package com.outr.arango

sealed trait DatabaseState

object DatabaseState {
  case object Uninitialized extends DatabaseState
  case object Initializing extends DatabaseState
  case class Initialized(session: ArangoSession, startupTime: Long) extends DatabaseState
  case class Error(throwable: Throwable) extends DatabaseState
}