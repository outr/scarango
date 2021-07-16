package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class DeleteAPITasksRc200(code: Double,
                               error: Option[Boolean] = None)

object DeleteAPITasksRc200 {
  implicit val rw: ReaderWriter[DeleteAPITasksRc200] = ccRW
}