package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionAddHttpExamplesRc404(error: Boolean,
                                                            code: Option[Int] = None,
                                                            errorMessage: Option[String] = None,
                                                            errorNum: Option[Int] = None)

object GeneralGraphVertexCollectionAddHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionAddHttpExamplesRc404] = ccRW
}