package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphListEdgeHttpExamplesRc404(error: Boolean,
                                                 code: Option[Int] = None,
                                                 errorMessage: Option[String] = None,
                                                 errorNum: Option[Int] = None)

object GeneralGraphListEdgeHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphListEdgeHttpExamplesRc404] = ccRW
}