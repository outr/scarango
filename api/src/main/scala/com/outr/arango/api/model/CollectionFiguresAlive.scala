package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CollectionFiguresAlive(count: Option[Long] = None,
                                  size: Option[Long] = None)

object CollectionFiguresAlive {
  implicit val rw: ReaderWriter[CollectionFiguresAlive] = ccRW
}