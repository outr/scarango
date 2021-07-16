package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeGetHttpExamplesRc304(error: Boolean,
                                                code: Option[Int] = None,
                                                errorMessage: Option[String] = None,
                                                errorNum: Option[Int] = None)

object GeneralGraphEdgeGetHttpExamplesRc304 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeGetHttpExamplesRc304] = ccRW
}