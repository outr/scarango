package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionAddHttpExamplesRc202(error: Boolean,
                                                            code: Option[Int] = None,
                                                            graph: Option[GraphRepresentation] = None)

object GeneralGraphVertexCollectionAddHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionAddHttpExamplesRc202] = ccRW
}