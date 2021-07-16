package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionAddHttpExamplesRc403(error: Boolean,
                                                          code: Option[Int] = None,
                                                          errorMessage: Option[String] = None,
                                                          errorNum: Option[Int] = None)

object GeneralGraphEdgeDefinitionAddHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionAddHttpExamplesRc403] = ccRW
}