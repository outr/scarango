package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class RestRemoveByKeys(collection: String,
                            keys: Option[List[String]] = None,
                            options: Option[PutAPISimpleRemoveByKeysOpts] = None)

object RestRemoveByKeys {
  implicit val rw: ReaderWriter[RestRemoveByKeys] = ccRW
}