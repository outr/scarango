package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeCreateHttpExamplesRc201(error: Boolean,
                                                   code: Option[Int] = None,
                                                   edge: Option[EdgeRepresentation] = None,
                                                   `new`: Option[EdgeRepresentation] = None)

object GeneralGraphEdgeCreateHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeCreateHttpExamplesRc201] = ccRW
}