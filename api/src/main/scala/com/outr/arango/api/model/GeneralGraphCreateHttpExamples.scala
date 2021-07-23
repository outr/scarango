package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphCreateHttpExamples(name: String,
                                          edgeDefinitions: Option[GraphEdgeDefinition] = None,
                                          isSmart: Option[Boolean] = None,
                                          options: Option[PostAPIGharialCreateOpts] = None)

object GeneralGraphCreateHttpExamples {
  implicit val rw: ReaderWriter[GeneralGraphCreateHttpExamples] = ccRW
}