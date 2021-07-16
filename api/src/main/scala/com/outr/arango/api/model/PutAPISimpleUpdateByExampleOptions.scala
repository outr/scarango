package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleUpdateByExampleOptions(keepNull: Option[Boolean] = None,
                                              limit: Option[Long] = None,
                                              mergeObjects: Option[Boolean] = None,
                                              waitForSync: Option[Boolean] = None)

object PutAPISimpleUpdateByExampleOptions {
  implicit val rw: ReaderWriter[PutAPISimpleUpdateByExampleOptions] = ccRW
}