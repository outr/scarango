package com.outr.arango

object IndexType {
  case object Hash extends IndexType
  case object SkipList extends IndexType
  case object Persistent extends IndexType
  case object Geo extends IndexType
  case object FullText extends IndexType
  case object TTL extends IndexType
}

sealed trait IndexType