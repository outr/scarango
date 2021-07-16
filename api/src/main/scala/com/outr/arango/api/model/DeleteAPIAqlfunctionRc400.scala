package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class DeleteAPIAqlfunctionRc400(error: Boolean,
                                     code: Option[Long] = None,
                                     errorMessage: Option[String] = None,
                                     errorNum: Option[Long] = None)

object DeleteAPIAqlfunctionRc400 {
  implicit val rw: ReaderWriter[DeleteAPIAqlfunctionRc400] = ccRW
}