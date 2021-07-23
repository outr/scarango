package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphEdgeReplaceHttpExamplesRc202(error: Boolean,
                                                    code: Option[Int] = None,
                                                    edge: Option[EdgeRepresentation] = None,
                                                    `new`: Option[EdgeRepresentation] = None,
                                                    old: Option[EdgeRepresentation] = None)

object GeneralGraphEdgeReplaceHttpExamplesRc202 {
  implicit val rw: ReaderWriter[GeneralGraphEdgeReplaceHttpExamplesRc202] = ccRW
}