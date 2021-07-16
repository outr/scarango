package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresJournals(count: Option[Long] = None,
                                     fileSize: Option[Long] = None)

object CollectionFiguresJournals {
  implicit val rw: ReaderWriter[CollectionFiguresJournals] = ccRW
}