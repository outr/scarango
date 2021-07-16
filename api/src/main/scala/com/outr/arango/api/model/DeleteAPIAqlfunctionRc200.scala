package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class DeleteAPIAqlfunctionRc200(error: Boolean,
                                     code: Option[Long] = None,
                                     deletedCount: Option[Long] = None)

object DeleteAPIAqlfunctionRc200 {
  implicit val rw: ReaderWriter[DeleteAPIAqlfunctionRc200] = ccRW
}