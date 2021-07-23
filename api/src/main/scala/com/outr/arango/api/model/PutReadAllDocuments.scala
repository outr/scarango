package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class PutReadAllDocuments(collection: String,
                               `type`: Option[String] = None)

object PutReadAllDocuments {
  implicit val rw: ReaderWriter[PutReadAllDocuments] = ccRW
}
