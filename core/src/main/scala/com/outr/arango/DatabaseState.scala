package com.outr.arango

sealed trait DatabaseState

object DatabaseState {
  case object Uninitialized extends DatabaseState
  case object Initializing extends DatabaseState
  case object Upgrading extends DatabaseState
  case class Initialized(session: ArangoDBSession, startupTime: Long) extends DatabaseState
  case class Error(throwable: Throwable) extends DatabaseState
}