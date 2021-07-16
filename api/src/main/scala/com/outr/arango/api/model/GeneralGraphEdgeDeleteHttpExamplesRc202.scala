package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDeleteHttpExamplesRc202(error: Boolean,
                                                   code: Option[Int] = None,
                                                   old: Option[EdgeRepresentation] = None,
                                                   removed: Option[Boolean] = None)

object GeneralGraphEdgeDeleteHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDeleteHttpExamplesRc202] = ccRW
}