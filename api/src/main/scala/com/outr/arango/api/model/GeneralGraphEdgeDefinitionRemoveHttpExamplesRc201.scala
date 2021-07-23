package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionRemoveHttpExamplesRc201(error: Boolean,
                                                             code: Option[Int] = None,
                                                             graph: Option[GraphRepresentation] = None)

object GeneralGraphEdgeDefinitionRemoveHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionRemoveHttpExamplesRc201] = ccRW
}