package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionModifyHttpExamplesRc404(error: Boolean,
                                                             code: Option[Int] = None,
                                                             errorMessage: Option[String] = None,
                                                             errorNum: Option[Int] = None)

object GeneralGraphEdgeDefinitionModifyHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionModifyHttpExamplesRc404] = ccRW
}