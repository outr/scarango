package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeGetHttpExamplesRc404(error: Boolean,
                                                code: Option[Int] = None,
                                                errorMessage: Option[String] = None,
                                                errorNum: Option[Int] = None)

object GeneralGraphEdgeGetHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeGetHttpExamplesRc404] = ccRW
}