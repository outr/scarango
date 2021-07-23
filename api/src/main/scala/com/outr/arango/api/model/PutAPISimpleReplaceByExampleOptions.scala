package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleReplaceByExampleOptions(limit: Option[String] = None,
                                               waitForSync: Option[Boolean] = None)

object PutAPISimpleReplaceByExampleOptions {
  implicit val rw: ReaderWriter[PutAPISimpleReplaceByExampleOptions] = ccRW
}