package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAdminTimeRc200(error: Boolean,
                             code: Option[Long] = None,
                             time: Option[Double] = None)

object GetAdminTimeRc200 {
  implicit val rw: ReaderWriter[GetAdminTimeRc200] = ccRW
}