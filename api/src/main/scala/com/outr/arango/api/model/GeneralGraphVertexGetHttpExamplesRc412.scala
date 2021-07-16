package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexGetHttpExamplesRc412(error: Boolean,
                                                  code: Option[Int] = None,
                                                  errorMessage: Option[String] = None,
                                                  errorNum: Option[Int] = None)

object GeneralGraphVertexGetHttpExamplesRc412 {
  implicit val rw: ReaderWriter[GeneralGraphVertexGetHttpExamplesRc412] = ccRW
}