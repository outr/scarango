package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GraphList(graph: Option[GraphRepresentation] = None)

object GraphList {
  implicit val rw: ReaderWriter[GraphList] = ccRW
}