package com.outr.arango.core

sealed trait TransactionStatus

object TransactionStatus {
  case object Running extends TransactionStatus
  case object Committed extends TransactionStatus
  case object Aborted extends TransactionStatus
}