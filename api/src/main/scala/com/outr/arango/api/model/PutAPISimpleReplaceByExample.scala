package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleReplaceByExample(collection: String,
                                        example: Option[String] = None,
                                        newValue: Option[String] = None,
                                        options: Option[PutAPISimpleReplaceByExampleOptions] = None)

object PutAPISimpleReplaceByExample {
  implicit val rw: ReaderWriter[PutAPISimpleReplaceByExample] = ccRW
}