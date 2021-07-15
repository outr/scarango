package com.outr.arango.api.model

import fabric.rw._

case class GetAPIAqlfunctionRc400(error: Boolean,
                                  code: Option[Long] = None,
                                  errorMessage: Option[String] = None,
                                  errorNum: Option[Long] = None)

object GetAPIAqlfunctionRc400 {
  implicit val rw: ReaderWriter[GetAPIAqlfunctionRc400] = ccRW
}