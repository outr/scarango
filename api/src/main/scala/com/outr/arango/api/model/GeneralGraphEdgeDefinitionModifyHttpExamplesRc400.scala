package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionModifyHttpExamplesRc400(error: Boolean,
                                                             code: Option[Int] = None,
                                                             errorMessage: Option[String] = None,
                                                             errorNum: Option[Int] = None)

object GeneralGraphEdgeDefinitionModifyHttpExamplesRc400 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionModifyHttpExamplesRc400] = ccRW
}