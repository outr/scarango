package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphListVertexHttpExamplesRc200(error: Boolean,
                                                   code: Option[Int] = None,
                                                   collections: Option[List[String]] = None)

object GeneralGraphListVertexHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphListVertexHttpExamplesRc200] = ccRW
}