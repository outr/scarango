package com.outr.arango.model

import fabric.rw.{ReaderWriter, ccRW}

case class DatabaseInfo(name: String, id: String, path: String, isSystem: Boolean)

object DatabaseInfo {
  implicit val rw: ReaderWriter[DatabaseInfo] = ccRW
}