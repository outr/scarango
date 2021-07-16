package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphGetHttpExamplesRc200(error: Boolean,
                                            code: Option[Int] = None,
                                            graph: Option[GraphRepresentation] = None)

object GeneralGraphGetHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphGetHttpExamplesRc200] = ccRW
}