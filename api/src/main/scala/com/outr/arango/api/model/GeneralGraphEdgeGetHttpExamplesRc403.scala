package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeGetHttpExamplesRc403(error: Boolean,
                                                code: Option[Int] = None,
                                                errorMessage: Option[String] = None,
                                                errorNum: Option[Int] = None)

object GeneralGraphEdgeGetHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeGetHttpExamplesRc403] = ccRW
}