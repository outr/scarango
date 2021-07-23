package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexReplaceHttpExamplesRc403(error: Boolean,
                                                      code: Option[Int] = None,
                                                      errorMessage: Option[String] = None,
                                                      errorNum: Option[Int] = None)

object GeneralGraphVertexReplaceHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphVertexReplaceHttpExamplesRc403] = ccRW
}