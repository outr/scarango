package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleRange(collection: String,
                             attribute: Option[String] = None,
                             closed: Option[Boolean] = None,
                             left: Option[String] = None,
                             limit: Option[Long] = None,
                             right: Option[String] = None,
                             skip: Option[String] = None)

object PutAPISimpleRange {
  implicit val rw: ReaderWriter[PutAPISimpleRange] = ccRW
}