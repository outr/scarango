package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class IndexDelete(id: String, error: Boolean, code: Int)

object IndexDelete {
  implicit val rw: ReaderWriter[IndexDelete] = ccRW
}