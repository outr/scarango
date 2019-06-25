package com.outr.arango.model

case class ArangoResponse[R](error: Boolean,
                             errorMessage: Option[String],
                             errorNum: Int = -1,
                             code: Int,
                             result: Option[R]) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum)

  def value: R = if (!error) {
    result.getOrElse(throw new RuntimeException(s"No result defined for $this"))
  } else {
    throw ArangoResponseException(s"${errorMessage.getOrElse("-- No error message --")} ($errorNum)", code)
  }
}