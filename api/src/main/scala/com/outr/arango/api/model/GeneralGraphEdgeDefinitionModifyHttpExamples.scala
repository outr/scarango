package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionModifyHttpExamples(collection: String,
                                                        from: Option[List[String]] = None,
                                                        to: Option[List[String]] = None)

object GeneralGraphEdgeDefinitionModifyHttpExamples {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionModifyHttpExamples] = ccRW
}