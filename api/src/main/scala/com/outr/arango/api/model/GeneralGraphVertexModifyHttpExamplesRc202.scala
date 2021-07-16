package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexModifyHttpExamplesRc202(error: Boolean,
                                                     code: Option[Int] = None,
                                                     `new`: Option[VertexRepresentation] = None,
                                                     old: Option[VertexRepresentation] = None,
                                                     vertex: Option[VertexRepresentation] = None)

object GeneralGraphVertexModifyHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphVertexModifyHttpExamplesRc202] = ccRW
}