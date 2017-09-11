package com.outr.arango

case class ArangoError(error: Boolean, code: Int, errorNum: Option[Int], errorMessage: String) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum.getOrElse(ArangoCode.Failed.code))

  def is(code: ArangoCode): Boolean = code == errorCode

  override def toString: String = s"error: $error, message: $errorMessage, code: $errorCode"
}