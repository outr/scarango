package com.outr.arango.transaction

import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}

sealed trait TransactionStatus

object TransactionStatus {
  case object Running extends TransactionStatus
  case object Committed extends TransactionStatus
  case object Aborted extends TransactionStatus

  implicit val decoder: Decoder[TransactionStatus] = new Decoder[TransactionStatus] {
    override def apply(c: HCursor): Result[TransactionStatus] = c.value.asString match {
      case Some(s) => Right(TransactionStatus(s))
      case None => Left(DecodingFailure(s"Failed to decode from ${c.value}", Nil))
    }
  }

  def apply(value: String): TransactionStatus = value match {
    case "running" => Running
    case "committed" => Committed
    case "aborted" => Aborted
  }
}