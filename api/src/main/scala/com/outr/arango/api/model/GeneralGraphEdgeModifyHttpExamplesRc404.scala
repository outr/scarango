package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeModifyHttpExamplesRc404(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphEdgeModifyHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeModifyHttpExamplesRc404] = ccRW
}