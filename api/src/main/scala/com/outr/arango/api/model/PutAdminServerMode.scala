package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAdminServerMode(mode: String)

object PutAdminServerMode {
  implicit val rw: ReaderWriter[PutAdminServerMode] = ccRW
}