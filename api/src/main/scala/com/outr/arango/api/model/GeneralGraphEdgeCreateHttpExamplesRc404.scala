package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeCreateHttpExamplesRc404(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphEdgeCreateHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeCreateHttpExamplesRc404] = ccRW
}