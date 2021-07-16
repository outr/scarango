package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphListVertexHttpExamplesRc404(error: Boolean,
                                                   code: Option[Int] = None,
                                                   errorMessage: Option[String] = None,
                                                   errorNum: Option[Int] = None)

object GeneralGraphListVertexHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphListVertexHttpExamplesRc404] = ccRW
}