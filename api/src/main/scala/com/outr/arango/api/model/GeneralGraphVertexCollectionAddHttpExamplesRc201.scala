package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionAddHttpExamplesRc201(error: Boolean,
                                                            code: Option[Int] = None,
                                                            graph: Option[GraphRepresentation] = None)

object GeneralGraphVertexCollectionAddHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionAddHttpExamplesRc201] = ccRW
}