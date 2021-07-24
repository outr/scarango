package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class TruncateCollectionResponse(id: String,
                                      name: String,
                                      isSystem: Boolean,
                                      status: Int,
                                      `type`: Int,
                                      error: Boolean,
                                      code: Int)

object TruncateCollectionResponse {
  implicit val rw: ReaderWriter[TruncateCollectionResponse] = ccRW
}