package com.outr.arango

import com.outr.arango.model.ArangoCode
import fabric.rw.{ReaderWriter, ccRW}

case class ArangoError(error: Boolean, code: Int, errorNum: Option[Int], errorMessage: String) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum.getOrElse(ArangoCode.Failed.code))

  def is(code: ArangoCode): Boolean = code == errorCode

  override def toString: String = s"error: $error, message: $errorMessage, code: $errorCode"
}

object ArangoError {
  implicit val rw: ReaderWriter[ArangoError] = ccRW
}