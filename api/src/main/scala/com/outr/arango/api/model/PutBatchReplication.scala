package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutBatchReplication(ttl: Long)

object PutBatchReplication {
  implicit val rw: ReaderWriter[PutBatchReplication] = ccRW
}