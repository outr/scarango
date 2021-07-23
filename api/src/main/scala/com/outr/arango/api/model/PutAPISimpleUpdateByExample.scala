package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleUpdateByExample(collection: String,
                                       example: Option[String] = None,
                                       newValue: Option[Value] = None,
                                       options: Option[PutAPISimpleUpdateByExampleOptions] = None)

object PutAPISimpleUpdateByExample {
  implicit val rw: ReaderWriter[PutAPISimpleUpdateByExample] = ccRW
}