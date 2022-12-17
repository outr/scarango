package com.outr.arango

object IndexType {
  case object Persistent extends IndexType
  case object Geo extends IndexType
  case object TTL extends IndexType
}

sealed trait IndexType