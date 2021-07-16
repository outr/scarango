package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionRemoveHttpExamplesRc404(error: Boolean,
                                                               code: Option[Int] = None,
                                                               errorMessage: Option[String] = None,
                                                               errorNum: Option[Int] = None)

object GeneralGraphVertexCollectionRemoveHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionRemoveHttpExamplesRc404] = ccRW
}