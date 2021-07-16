package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexGetHttpExamplesRc403(error: Boolean,
                                                  code: Option[Int] = None,
                                                  errorMessage: Option[String] = None,
                                                  errorNum: Option[Int] = None)

object GeneralGraphVertexGetHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphVertexGetHttpExamplesRc403] = ccRW
}