package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexDeleteHttpExamplesRc404(error: Boolean,
                                                     code: Option[Int] = None,
                                                     errorMessage: Option[String] = None,
                                                     errorNum: Option[Int] = None)

object GeneralGraphVertexDeleteHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphVertexDeleteHttpExamplesRc404] = ccRW
}