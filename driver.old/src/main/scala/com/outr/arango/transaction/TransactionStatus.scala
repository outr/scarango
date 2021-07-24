package com.outr.arango.transaction

import fabric._
import fabric.rw.{ReaderWriter, Writer}

sealed trait TransactionStatus

object TransactionStatus {
  case object Running extends TransactionStatus
  case object Committed extends TransactionStatus
  case object Aborted extends TransactionStatus

  implicit val rw: ReaderWriter[TransactionStatus] = ReaderWriter(t => str(t.getClass.getSimpleName.toLowerCase), v => apply(v.asStr.value))

  def apply(value: String): TransactionStatus = value match {
    case "running" => Running
    case "committed" => Committed
    case "aborted" => Aborted
  }
}