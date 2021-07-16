package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexReplaceHttpExamplesRc412(error: Boolean,
                                                      code: Option[Int] = None,
                                                      errorMessage: Option[String] = None,
                                                      errorNum: Option[Int] = None)

object GeneralGraphVertexReplaceHttpExamplesRc412 {
  implicit val rw: ReaderWriter[GeneralGraphVertexReplaceHttpExamplesRc412] = ccRW
}