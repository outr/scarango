package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeCreateHttpExamplesRc403(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphEdgeCreateHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeCreateHttpExamplesRc403] = ccRW
}