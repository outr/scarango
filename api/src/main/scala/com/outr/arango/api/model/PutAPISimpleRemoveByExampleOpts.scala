package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleRemoveByExampleOpts(limit: Option[String] = None,
                                           waitForSync: Option[Boolean] = None)

object PutAPISimpleRemoveByExampleOpts {
  implicit val rw: ReaderWriter[PutAPISimpleRemoveByExampleOpts] = ccRW
}