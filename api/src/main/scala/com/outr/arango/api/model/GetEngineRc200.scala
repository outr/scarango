package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetEngineRc200(name: String)

object GetEngineRc200 {
  implicit val rw: ReaderWriter[GetEngineRc200] = ccRW
}