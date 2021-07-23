package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIViewFields(`field-name`: String)

object PostAPIViewFields {
  implicit val rw: ReaderWriter[PostAPIViewFields] = ccRW
}