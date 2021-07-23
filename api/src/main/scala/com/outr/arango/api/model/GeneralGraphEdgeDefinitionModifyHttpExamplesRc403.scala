package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionModifyHttpExamplesRc403(error: Boolean,
                                                             code: Option[Int] = None,
                                                             errorMessage: Option[String] = None,
                                                             errorNum: Option[Int] = None)

object GeneralGraphEdgeDefinitionModifyHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionModifyHttpExamplesRc403] = ccRW
}