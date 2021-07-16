package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphVertexDeleteHttpExamplesRc200(error: Boolean,
                                                     code: Option[Int] = None,
                                                     old: Option[VertexRepresentation] = None,
                                                     removed: Option[Boolean] = None)

object GeneralGraphVertexDeleteHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphVertexDeleteHttpExamplesRc200] = ccRW
}