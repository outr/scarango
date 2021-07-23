package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexCreateHttpExamplesRc201(error: Boolean,
                                                     code: Option[Int] = None,
                                                     `new`: Option[VertexRepresentation] = None,
                                                     vertex: Option[VertexRepresentation] = None)

object GeneralGraphVertexCreateHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphVertexCreateHttpExamplesRc201] = ccRW
}