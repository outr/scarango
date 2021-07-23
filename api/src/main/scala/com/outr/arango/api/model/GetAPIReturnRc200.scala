package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAPIReturnRc200(server: String,
                             details: Option[VersionDetailsStruct] = None,
                             version: Option[String] = None)

object GetAPIReturnRc200 {
  implicit val rw: ReaderWriter[GetAPIReturnRc200] = ccRW
}