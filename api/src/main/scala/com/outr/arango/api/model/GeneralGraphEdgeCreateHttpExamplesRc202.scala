package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeCreateHttpExamplesRc202(error: Boolean,
                                                   code: Option[Int] = None,
                                                   edge: Option[EdgeRepresentation] = None,
                                                   `new`: Option[EdgeRepresentation] = None)

object GeneralGraphEdgeCreateHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeCreateHttpExamplesRc202] = ccRW
}