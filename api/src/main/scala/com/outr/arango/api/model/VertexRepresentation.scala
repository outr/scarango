package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class VertexRepresentation(Id: Option[String] = None,
                                Key: Option[String] = None,
                                Rev: Option[String] = None)

object VertexRepresentation {
  implicit val rw: ReaderWriter[VertexRepresentation] = ccRW
}