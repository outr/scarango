package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIViewLinks(collectionName: Option[PostAPIViewLinkProps] = None)

object PostAPIViewLinks {
  implicit val rw: ReaderWriter[PostAPIViewLinks] = ccRW
}