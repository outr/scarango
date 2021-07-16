package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeModifyHttpExamplesRc200(error: Boolean,
                                                   code: Option[Int] = None,
                                                   edge: Option[EdgeRepresentation] = None,
                                                   `new`: Option[EdgeRepresentation] = None,
                                                   old: Option[EdgeRepresentation] = None)

object GeneralGraphEdgeModifyHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeModifyHttpExamplesRc200] = ccRW
}