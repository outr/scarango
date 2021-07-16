package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPICursor(query: String,
                         batchSize: Option[Long] = None,
                         bindVars: Value,
                         cache: Option[Boolean] = None,
                         count: Option[Boolean] = None,
                         memoryLimit: Option[Long] = None,
                         options: Option[PostAPICursorOpts] = None,
                         ttl: Option[Long] = None)

object PostAPICursor {
  implicit val rw: ReaderWriter[PostAPICursor] = ccRW
}