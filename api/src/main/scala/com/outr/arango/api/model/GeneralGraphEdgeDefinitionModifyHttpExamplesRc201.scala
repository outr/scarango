package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionModifyHttpExamplesRc201(error: Boolean,
                                                             code: Option[Int] = None,
                                                             graph: Option[GraphRepresentation] = None)

object GeneralGraphEdgeDefinitionModifyHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionModifyHttpExamplesRc201] = ccRW
}