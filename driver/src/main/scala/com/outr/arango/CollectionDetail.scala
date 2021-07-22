package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class CollectionDetail(id: String,
                            name: String,
                            isSystem: Boolean,
                            status: Int,
                            `type`: Int)

object CollectionDetail {
  implicit val rw: ReaderWriter[CollectionDetail] = ccRW
}