package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAPITasksAllRc200()

object GetAPITasksAllRc200 {
  implicit val rw: ReaderWriter[GetAPITasksAllRc200] = ccRW
}