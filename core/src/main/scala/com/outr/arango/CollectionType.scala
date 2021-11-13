package com.outr.arango

sealed trait CollectionType

object CollectionType {
  case object Vertex extends CollectionType
  case object Edge extends CollectionType
}