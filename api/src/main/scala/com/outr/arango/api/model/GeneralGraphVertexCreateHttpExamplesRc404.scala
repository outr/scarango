package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCreateHttpExamplesRc404(error: Boolean,
                                                     code: Option[Int] = None,
                                                     errorMessage: Option[String] = None,
                                                     errorNum: Option[Int] = None)

object GeneralGraphVertexCreateHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCreateHttpExamplesRc404] = ccRW
}