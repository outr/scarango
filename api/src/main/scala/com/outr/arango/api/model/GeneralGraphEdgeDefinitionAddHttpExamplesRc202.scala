package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionAddHttpExamplesRc202(error: Boolean,
                                                          code: Option[Int] = None,
                                                          graph: Option[GraphRepresentation] = None)

object GeneralGraphEdgeDefinitionAddHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionAddHttpExamplesRc202] = ccRW
}