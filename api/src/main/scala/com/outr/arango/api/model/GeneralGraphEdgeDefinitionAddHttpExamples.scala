package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeDefinitionAddHttpExamples(collection: String,
                                                     from: Option[List[String]] = None,
                                                     to: Option[List[String]] = None)

object GeneralGraphEdgeDefinitionAddHttpExamples {
  implicit val rw: ReaderWriter[GeneralGraphEdgeDefinitionAddHttpExamples] = ccRW
}