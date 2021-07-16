package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphCreateHttpExamplesRc201(error: Boolean,
                                               code: Option[Int] = None,
                                               graph: Option[GraphRepresentation] = None)

object GeneralGraphCreateHttpExamplesRc201 {
  implicit val rw: ReaderWriter[GeneralGraphCreateHttpExamplesRc201] = ccRW
}