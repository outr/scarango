package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexModifyHttpExamplesRc404(error: Boolean,
                                                     code: Option[Int] = None,
                                                     errorMessage: Option[String] = None,
                                                     errorNum: Option[Int] = None)

object GeneralGraphVertexModifyHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphVertexModifyHttpExamplesRc404] = ccRW
}