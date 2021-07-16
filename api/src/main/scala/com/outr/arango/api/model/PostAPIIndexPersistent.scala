package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIIndexPersistent(`type`: String,
                                  fields: Option[List[String]] = None,
                                  sparse: Option[Boolean] = None,
                                  unique: Option[Boolean] = None)

object PostAPIIndexPersistent {
  implicit val rw: ReaderWriter[PostAPIIndexPersistent] = ccRW
}