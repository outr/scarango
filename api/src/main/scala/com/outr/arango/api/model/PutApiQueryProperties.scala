package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutApiQueryProperties(enabled: Boolean,
                                 maxQueryStringLength: Option[Long] = None,
                                 maxSlowQueries: Option[Long] = None,
                                 slowQueryThreshold: Option[Long] = None,
                                 trackBindVars: Option[Boolean] = None,
                                 trackSlowQueries: Option[Boolean] = None)

object PutApiQueryProperties {
  implicit val rw: ReaderWriter[PutApiQueryProperties] = ccRW
}