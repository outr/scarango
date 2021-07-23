package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDeleteHttpExamplesRc404(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphEdgeDeleteHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDeleteHttpExamplesRc404] = ccRW
}