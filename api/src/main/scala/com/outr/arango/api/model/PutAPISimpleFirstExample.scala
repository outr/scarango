package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleFirstExample(collection: String,
                                    example: Option[String] = None)

object PutAPISimpleFirstExample {
  implicit val rw: ReaderWriter[PutAPISimpleFirstExample] = ccRW
}