package com.outr.arango

import fabric.rw.{ReaderWriter, ccRW}

case class DatabaseVersion(upgrades: Set[String] = Set.empty)

object DatabaseVersion {
  implicit val rw: ReaderWriter[DatabaseVersion] = ccRW
}