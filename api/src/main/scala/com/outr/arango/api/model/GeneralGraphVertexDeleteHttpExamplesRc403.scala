package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexDeleteHttpExamplesRc403(error: Boolean,
                                                     code: Option[Int] = None,
                                                     errorMessage: Option[String] = None,
                                                     errorNum: Option[Int] = None)

object GeneralGraphVertexDeleteHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphVertexDeleteHttpExamplesRc403] = ccRW
}