package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionAddHttpExamplesRc403(error: Boolean,
                                                            code: Option[Int] = None,
                                                            errorMessage: Option[String] = None,
                                                            errorNum: Option[Int] = None)

object GeneralGraphVertexCollectionAddHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionAddHttpExamplesRc403] = ccRW
}