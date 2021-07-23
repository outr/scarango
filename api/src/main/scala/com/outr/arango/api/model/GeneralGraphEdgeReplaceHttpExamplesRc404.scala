package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeReplaceHttpExamplesRc404(error: Boolean,
                                                    code: Option[Int] = None,
                                                    errorMessage: Option[String] = None,
                                                    errorNum: Option[Int] = None)

object GeneralGraphEdgeReplaceHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeReplaceHttpExamplesRc404] = ccRW
}