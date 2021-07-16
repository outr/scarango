package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GraphEdgeDefinition(collection: Option[String] = None,
                               from: Option[List[String]] = None,
                               to: Option[List[String]] = None)

object GraphEdgeDefinition {
  implicit val rw: ReaderWriter[GraphEdgeDefinition] = ccRW
}