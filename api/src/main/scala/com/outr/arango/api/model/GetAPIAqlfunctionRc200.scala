package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class GetAPIAqlfunctionRc200(error: Boolean,
                                  code: Option[Long] = None,
                                  result: Option[AqlUserfunctionStruct] = None)

object GetAPIAqlfunctionRc200 {
  implicit val rw: ReaderWriter[GetAPIAqlfunctionRc200] = ccRW
}