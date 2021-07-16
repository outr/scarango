package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexModifyHttpExamplesRc200(error: Boolean,
                                                     code: Option[Int] = None,
                                                     `new`: Option[VertexRepresentation] = None,
                                                     old: Option[VertexRepresentation] = None,
                                                     vertex: Option[VertexRepresentation] = None)

object GeneralGraphVertexModifyHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphVertexModifyHttpExamplesRc200] = ccRW
}