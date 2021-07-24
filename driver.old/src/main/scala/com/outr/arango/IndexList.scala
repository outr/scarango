package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class IndexList(indexes: List[IndexInfo], error: Boolean, code: Int)

object IndexList {
  implicit val rw: ReaderWriter[IndexList] = ccRW
}