package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleRemoveByKeysOpts(returnOld: Option[Boolean] = None,
                                        silent: Option[Boolean] = None,
                                        waitForSync: Option[Boolean] = None)

object PutAPISimpleRemoveByKeysOpts {
  implicit val rw: ReaderWriter[PutAPISimpleRemoveByKeysOpts] = ccRW
}