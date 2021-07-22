package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class ViewDetail(globallyUniqueId: String,
                      id: String,
                      name: String,
                      `type`: String)

object ViewDetail {
  implicit val rw: ReaderWriter[ViewDetail] = ccRW
}