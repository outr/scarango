package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionRemoveHttpExamplesRc200(error: Boolean,
                                                               code: Option[Int] = None,
                                                               graph: Option[GraphRepresentation] = None)

object GeneralGraphVertexCollectionRemoveHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionRemoveHttpExamplesRc200] = ccRW
}