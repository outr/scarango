package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresReadcache(count: Option[Long] = None,
                                      size: Option[Long] = None)

object CollectionFiguresReadcache {
  implicit val rw: ReaderWriter[CollectionFiguresReadcache] = ccRW
}