package com.outr.arango.model

import fabric.Value
import fabric.rw.{Asable, ReaderWriter, Writer, ccRW}

case class ArangoResponse(error: Boolean,
                          errorMessage: Option[String],
                          errorNum: Int = -1,
                          code: Int,
                          result: Option[Value]) {
  lazy val errorCode: ArangoCode = ArangoCode(errorNum)

  def value[R: Writer]: R = if (!error) {
    result.map(_.as[R]).getOrElse(throw new RuntimeException(s"No result defined for $this"))
  } else {
    throw ArangoResponseException(s"${errorMessage.getOrElse("-- No error message --")} ($errorNum)", code)
  }
}

object ArangoResponse {
  implicit def rw[R]: ReaderWriter[ArangoResponse] = ccRW
}