package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphListEdgeHttpExamplesRc200(error: Boolean,
                                                 code: Option[Int] = None,
                                                 collections: Option[List[String]] = None)

object GeneralGraphListEdgeHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphListEdgeHttpExamplesRc200] = ccRW
}