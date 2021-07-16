package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeModifyHttpExamplesRc403(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphEdgeModifyHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeModifyHttpExamplesRc403] = ccRW
}