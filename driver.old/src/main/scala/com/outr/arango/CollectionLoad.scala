package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class CollectionLoad(id: String,
                          name: String,
                          count: Option[Int],
                          status: Int,
                          `type`: Int,
                          isSystem: Boolean)

object CollectionLoad {
  implicit val rw: ReaderWriter[CollectionLoad] = ccRW
}