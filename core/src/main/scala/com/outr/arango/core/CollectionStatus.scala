package com.outr.arango.core

sealed trait CollectionStatus

object CollectionStatus {
  case object Loaded extends CollectionStatus
  case object Deleted extends CollectionStatus
}