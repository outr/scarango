package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleAny(collection: String)

object PutAPISimpleAny {
  implicit val rw: ReaderWriter[PutAPISimpleAny] = ccRW
}