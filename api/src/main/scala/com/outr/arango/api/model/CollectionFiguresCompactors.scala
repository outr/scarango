package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresCompactors(count: Option[Long] = None,
                                       fileSize: Option[Long] = None)

object CollectionFiguresCompactors {
  implicit val rw: ReaderWriter[CollectionFiguresCompactors] = ccRW
}