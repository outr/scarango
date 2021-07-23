package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAdminServerRoleRc200(error: Boolean,
                                   code: Option[Long] = None,
                                   errorNum: Option[Long] = None,
                                   role: Option[String] = None)

object GetAdminServerRoleRc200 {
  implicit val rw: ReaderWriter[GetAdminServerRoleRc200] = ccRW
}