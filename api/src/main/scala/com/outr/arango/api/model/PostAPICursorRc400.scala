package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPICursorRc400(error: Boolean,
                              code: Option[Long] = None,
                              errorMessage: Option[String] = None,
                              errorNum: Option[Long] = None)

object PostAPICursorRc400 {
  implicit val rw: ReaderWriter[PostAPICursorRc400] = ccRW
}