package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionRemoveHttpExamplesRc403(error: Boolean,
                                                             code: Option[Int] = None,
                                                             errorMessage: Option[String] = None,
                                                             errorNum: Option[Int] = None)

object GeneralGraphEdgeDefinitionRemoveHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionRemoveHttpExamplesRc403] = ccRW
}