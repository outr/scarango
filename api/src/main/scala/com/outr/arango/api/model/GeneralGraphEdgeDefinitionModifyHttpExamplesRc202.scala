package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionModifyHttpExamplesRc202(error: Boolean,
                                                             code: Option[Int] = None,
                                                             graph: Option[GraphRepresentation] = None)

object GeneralGraphEdgeDefinitionModifyHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionModifyHttpExamplesRc202] = ccRW
}