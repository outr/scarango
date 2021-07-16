package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIExplain(query: String,
                          bindVars: Option[List[String]] = None,
                          options: Option[ExplainOptions] = None)

object PostAPIExplain {
  implicit val rw: ReaderWriter[PostAPIExplain] = ccRW
}