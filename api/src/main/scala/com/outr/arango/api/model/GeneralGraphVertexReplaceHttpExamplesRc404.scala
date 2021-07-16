package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexReplaceHttpExamplesRc404(error: Boolean,
                                                      code: Option[Int] = None,
                                                      errorMessage: Option[String] = None,
                                                      errorNum: Option[Int] = None)

object GeneralGraphVertexReplaceHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphVertexReplaceHttpExamplesRc404] = ccRW
}