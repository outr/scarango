package com.outr.arango.core

sealed trait KeyType

object KeyType {
  case object Traditional extends KeyType
  case object AutoIncrement extends KeyType
  case object UUID extends KeyType
  case object Padded extends KeyType
}