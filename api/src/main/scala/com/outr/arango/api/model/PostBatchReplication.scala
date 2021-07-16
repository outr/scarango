package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostBatchReplication(ttl: Long)

object PostBatchReplication {
  implicit val rw: ReaderWriter[PostBatchReplication] = ccRW
}