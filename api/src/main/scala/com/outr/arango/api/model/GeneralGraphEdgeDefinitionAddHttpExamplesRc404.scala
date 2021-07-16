package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionAddHttpExamplesRc404(error: Boolean,
                                                          code: Option[Int] = None,
                                                          errorMessage: Option[String] = None,
                                                          errorNum: Option[Int] = None)

object GeneralGraphEdgeDefinitionAddHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionAddHttpExamplesRc404] = ccRW
}