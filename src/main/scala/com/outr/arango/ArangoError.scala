package com.outr.arango

case class ArangoError(error: Boolean, code: Int, errorNum: Int, errorMessage: String) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum)

  def is(code: ArangoCode): Boolean = code.code == this.errorNum

  override def toString: String = s"error: $error, message: $errorMessage, code: $errorCode"
}