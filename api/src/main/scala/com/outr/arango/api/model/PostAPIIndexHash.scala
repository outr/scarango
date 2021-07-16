package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIIndexHash(`type`: String,
                            deduplicate: Option[Boolean] = None,
                            fields: Option[List[String]] = None,
                            sparse: Option[Boolean] = None,
                            unique: Option[Boolean] = None)

object PostAPIIndexHash {
  implicit val rw: ReaderWriter[PostAPIIndexHash] = ccRW
}