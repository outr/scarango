package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class DeleteAPITasksRc404(code: Double,
                               error: Option[Boolean] = None,
                               errorMessage: Option[String] = None)

object DeleteAPITasksRc404 {
  implicit val rw: ReaderWriter[DeleteAPITasksRc404] = ccRW
}