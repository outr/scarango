package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleRemoveByExample(collection: String,
                                       example: Option[String] = None,
                                       options: Option[PutAPISimpleRemoveByExampleOpts] = None)

object PutAPISimpleRemoveByExample {
  implicit val rw: ReaderWriter[PutAPISimpleRemoveByExample] = ccRW
}