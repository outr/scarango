package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class CreateDatabaseOptions(sharding: Option[String], replicationFactor: Option[String], writeConcern: Option[String])

object CreateDatabaseOptions {
  implicit val rw: ReaderWriter[CreateDatabaseOptions] = ccRW
}