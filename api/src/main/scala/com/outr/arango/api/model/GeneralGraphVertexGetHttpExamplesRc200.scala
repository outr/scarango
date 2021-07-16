package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexGetHttpExamplesRc200(error: Boolean,
                                                  code: Option[Int] = None,
                                                  vertex: Option[VertexRepresentation] = None)

object GeneralGraphVertexGetHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphVertexGetHttpExamplesRc200] = ccRW
}