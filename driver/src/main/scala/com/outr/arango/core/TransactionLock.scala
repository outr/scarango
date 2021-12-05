package com.outr.arango.core

sealed trait TransactionLock

object TransactionLock {
  case object Read extends TransactionLock
  case object Write extends TransactionLock
  case object Exclusive extends TransactionLock
}