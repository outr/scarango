package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDeleteHttpExamplesRc403(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphEdgeDeleteHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDeleteHttpExamplesRc403] = ccRW
}