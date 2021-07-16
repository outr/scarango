package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeReplaceHttpExamplesRc403(error: Boolean,
                                                    code: Option[Int] = None,
                                                    errorMessage: Option[String] = None,
                                                    errorNum: Option[Int] = None)

object GeneralGraphEdgeReplaceHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeReplaceHttpExamplesRc403] = ccRW
}