package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresDatafiles(count: Option[Long] = None,
                                      fileSize: Option[Long] = None)

object CollectionFiguresDatafiles {
  implicit val rw: ReaderWriter[CollectionFiguresDatafiles] = ccRW
}