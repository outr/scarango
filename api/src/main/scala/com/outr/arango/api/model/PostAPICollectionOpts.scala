package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPICollectionOpts(allowUserKeys: Option[Boolean] = None,
                                 increment: Option[Long] = None,
                                 offset: Option[Long] = None,
                                 `type`: Option[String] = None)

object PostAPICollectionOpts {
  implicit val rw: ReaderWriter[PostAPICollectionOpts] = ccRW
}