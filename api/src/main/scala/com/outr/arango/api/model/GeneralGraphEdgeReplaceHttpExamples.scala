package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeReplaceHttpExamples(From: String,
                                               To: Option[String] = None)

object GeneralGraphEdgeReplaceHttpExamples {
  implicit val rw: ReaderWriter[GeneralGraphEdgeReplaceHttpExamples] = ccRW
}