package com.outr.arango.core

sealed trait CollectionStatus

object CollectionStatus {
  case object New extends CollectionStatus
  case object Unloaded extends CollectionStatus
  case object Loaded extends CollectionStatus
  case object Loading extends CollectionStatus
  case object Deleted extends CollectionStatus
}