package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutApiQueryCacheProperties(includeSystem: Option[Boolean] = None,
                                      maxEntrySize: Option[Long] = None,
                                      maxResults: Option[Long] = None,
                                      maxResultsSize: Option[Long] = None,
                                      mode: Option[String] = None)

object PutApiQueryCacheProperties {
  implicit val rw: ReaderWriter[PutApiQueryCacheProperties] = ccRW
}