package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexModifyHttpExamplesRc412(error: Boolean,
                                                     code: Option[Int] = None,
                                                     errorMessage: Option[String] = None,
                                                     errorNum: Option[Int] = None)

object GeneralGraphVertexModifyHttpExamplesRc412 {
  implicit val rw: ReaderWriter[GeneralGraphVertexModifyHttpExamplesRc412] = ccRW
}