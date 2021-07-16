package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresRevisions(count: Option[Long] = None,
                                      size: Option[Long] = None)

object CollectionFiguresRevisions {
  implicit val rw: ReaderWriter[CollectionFiguresRevisions] = ccRW
}