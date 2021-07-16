package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAPICollectionFiguresRc200(count: Long,
                                        figures: Option[CollectionFigures] = None,
                                        journalSize: Option[Long] = None)

object GetAPICollectionFiguresRc200 {
  implicit val rw: ReaderWriter[GetAPICollectionFiguresRc200] = ccRW
}