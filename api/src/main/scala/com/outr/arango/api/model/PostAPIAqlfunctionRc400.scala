package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIAqlfunctionRc400(error: Boolean,
                                   code: Option[Long] = None,
                                   errorMessage: Option[String] = None,
                                   errorNum: Option[Long] = None)

object PostAPIAqlfunctionRc400 {
  implicit val rw: ReaderWriter[PostAPIAqlfunctionRc400] = ccRW
}