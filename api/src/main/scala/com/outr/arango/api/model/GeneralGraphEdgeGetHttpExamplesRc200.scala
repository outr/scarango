package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeGetHttpExamplesRc200(error: Boolean,
                                                code: Option[Int] = None,
                                                edge: Option[EdgeRepresentation] = None)

object GeneralGraphEdgeGetHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeGetHttpExamplesRc200] = ccRW
}