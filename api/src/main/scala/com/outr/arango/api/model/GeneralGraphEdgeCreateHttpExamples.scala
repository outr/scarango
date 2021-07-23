package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeCreateHttpExamples(From: String,
                                              To: Option[String] = None)

object GeneralGraphEdgeCreateHttpExamples {
  implicit val rw: ReaderWriter[GeneralGraphEdgeCreateHttpExamples] = ccRW
}