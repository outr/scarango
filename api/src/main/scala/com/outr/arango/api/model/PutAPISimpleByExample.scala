package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleByExample(collection: String,
                                 batchSize: Option[Long] = None,
                                 example: Option[String] = None,
                                 limit: Option[String] = None,
                                 skip: Option[String] = None)

object PutAPISimpleByExample {
  implicit val rw: ReaderWriter[PutAPISimpleByExample] = ccRW
}