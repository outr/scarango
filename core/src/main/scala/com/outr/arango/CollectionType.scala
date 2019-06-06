package com.outr.arango

sealed trait CollectionType

object CollectionType {
  case object Document extends CollectionType
  case object Edge extends CollectionType
}