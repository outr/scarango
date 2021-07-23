package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresDead(count: Option[Long] = None,
                                 deletion: Option[Long] = None,
                                 size: Option[Long] = None)

object CollectionFiguresDead {
  implicit val rw: ReaderWriter[CollectionFiguresDead] = ccRW
}