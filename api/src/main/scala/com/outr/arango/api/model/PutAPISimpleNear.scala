package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleNear(collection: String,
                            distance: Option[String] = None,
                            geo: Option[String] = None,
                            latitude: Option[String] = None,
                            limit: Option[String] = None,
                            longitude: Option[String] = None,
                            skip: Option[String] = None)

object PutAPISimpleNear {
  implicit val rw: ReaderWriter[PutAPISimpleNear] = ccRW
}