package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleWithin(collection: String,
                              distance: Option[String] = None,
                              geo: Option[String] = None,
                              latitude: Option[String] = None,
                              limit: Option[String] = None,
                              longitude: Option[String] = None,
                              radius: Option[String] = None,
                              skip: Option[String] = None)

object PutAPISimpleWithin {
  implicit val rw: ReaderWriter[PutAPISimpleWithin] = ccRW
}