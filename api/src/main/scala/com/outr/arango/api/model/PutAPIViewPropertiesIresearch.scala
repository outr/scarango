package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPIViewPropertiesIresearch(properties: Option[PostAPIViewProps] = None)

object PutAPIViewPropertiesIresearch {
  implicit val rw: ReaderWriter[PutAPIViewPropertiesIresearch] = ccRW
}