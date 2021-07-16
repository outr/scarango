package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCreateHttpExamplesRc202(error: Boolean,
                                                     code: Option[Int] = None,
                                                     `new`: Option[VertexRepresentation] = None,
                                                     vertex: Option[VertexRepresentation] = None)

object GeneralGraphVertexCreateHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCreateHttpExamplesRc202] = ccRW
}