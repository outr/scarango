package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PatchAPIViewPropertiesIresearch(properties: Option[PostAPIViewProps] = None)

object PatchAPIViewPropertiesIresearch {
  implicit val rw: ReaderWriter[PatchAPIViewPropertiesIresearch] = ccRW
}