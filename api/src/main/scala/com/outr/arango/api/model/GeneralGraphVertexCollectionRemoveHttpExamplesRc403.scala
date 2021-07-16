package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCollectionRemoveHttpExamplesRc403(error: Boolean,
                                                               code: Option[Int] = None,
                                                               errorMessage: Option[String] = None,
                                                               errorNum: Option[Int] = None)

object GeneralGraphVertexCollectionRemoveHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCollectionRemoveHttpExamplesRc403] = ccRW
}