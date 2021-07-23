package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIExport(flush: Boolean,
                         batchSize: Option[Long] = None,
                         count: Option[Boolean] = None,
                         flushWait: Option[Long] = None,
                         limit: Option[Long] = None,
                         restrict: Option[PostAPIExportRestrictions] = None,
                         ttl: Option[Long] = None)

object PostAPIExport {
  implicit val rw: ReaderWriter[PostAPIExport] = ccRW
}