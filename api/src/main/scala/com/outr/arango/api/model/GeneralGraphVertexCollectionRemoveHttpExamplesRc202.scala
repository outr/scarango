package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionRemoveHttpExamplesRc202(error: Boolean,
                                                               code: Option[Int] = None,
                                                               graph: Option[GraphRepresentation] = None)

object GeneralGraphVertexCollectionRemoveHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionRemoveHttpExamplesRc202] = ccRW
}