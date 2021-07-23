package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class GetAPIDatabaseNew(name: String,
                             options: Option[CreateDatabaseOptions] = None,
                             users: Option[GetAPIDatabaseNewUSERS] = None)

object GetAPIDatabaseNew {
  implicit val rw: ReaderWriter[GetAPIDatabaseNew] = ccRW
}