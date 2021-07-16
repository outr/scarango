package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresIndexes(count: Option[Long] = None,
                                    size: Option[Long] = None)

object CollectionFiguresIndexes {
  implicit val rw: ReaderWriter[CollectionFiguresIndexes] = ccRW
}