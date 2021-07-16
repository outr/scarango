package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIIndexFulltext(`type`: String,
                                fields: Option[List[String]] = None,
                                minLength: Option[Long] = None)

object PostAPIIndexFulltext {
  implicit val rw: ReaderWriter[PostAPIIndexFulltext] = ccRW
}