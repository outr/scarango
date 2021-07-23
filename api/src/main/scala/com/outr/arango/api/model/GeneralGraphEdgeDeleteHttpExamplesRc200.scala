package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDeleteHttpExamplesRc200(error: Boolean,
                                                   code: Option[Int] = None,
                                                   old: Option[EdgeRepresentation] = None,
                                                   removed: Option[Boolean] = None)

object GeneralGraphEdgeDeleteHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDeleteHttpExamplesRc200] = ccRW
}