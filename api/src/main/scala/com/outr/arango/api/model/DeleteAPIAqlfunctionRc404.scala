package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class DeleteAPIAqlfunctionRc404(error: Boolean,
                                     code: Option[Long] = None,
                                     errorMessage: Option[String] = None,
                                     errorNum: Option[Long] = None)

object DeleteAPIAqlfunctionRc404 {
  implicit val rw: ReaderWriter[DeleteAPIAqlfunctionRc404] = ccRW
}