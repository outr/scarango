package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIIndexTtl(`type`: String,
                           expireAfter: Option[Double] = None,
                           fields: Option[List[String]] = None)

object PostAPIIndexTtl {
  implicit val rw: ReaderWriter[PostAPIIndexTtl] = ccRW
}