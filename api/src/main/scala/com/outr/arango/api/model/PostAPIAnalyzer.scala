package com.outr.arango.api.model

import fabric.rw._

case class PostAPIAnalyzer(name: String,
                           features: Option[List[String]] = None,
                           properties: Option[String] = None,
                           `type`: Option[String] = None)

object PostAPIAnalyzer {
  implicit val rw: ReaderWriter[PostAPIAnalyzer] = ccRW
}