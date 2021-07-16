package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionAddHttpExamplesRc201(error: Boolean,
                                                          code: Option[Int] = None,
                                                          graph: Option[GraphRepresentation] = None)

object GeneralGraphEdgeDefinitionAddHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionAddHttpExamplesRc201] = ccRW
}