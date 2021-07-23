package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIExportRestrictions(fields: Option[List[String]] = None,
                                     `type`: Option[String] = None)

object PostAPIExportRestrictions {
  implicit val rw: ReaderWriter[PostAPIExportRestrictions] = ccRW
}