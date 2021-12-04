package com.outr.arango

case class ArangoError(code: Int, num: Int, message: String, exception: String) {
  lazy val errorCode: ArangoCode = ArangoCode(num)

  def is(code: ArangoCode): Boolean = code == errorCode

  override def toString: String = s"message: $message, exception: $exception, code: $errorCode"
}