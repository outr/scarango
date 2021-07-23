package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostApiQueryProperties(query: String)

object PostApiQueryProperties {
  implicit val rw: ReaderWriter[PostApiQueryProperties] = ccRW
}