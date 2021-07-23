package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class RestLookupByKeys(collection: String,
                            keys: Option[List[String]] = None)

object RestLookupByKeys {
  implicit val rw: ReaderWriter[RestLookupByKeys] = ccRW
}