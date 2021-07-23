package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPISimpleFulltext(collection: String,
                                attribute: Option[String] = None,
                                index: Option[String] = None,
                                limit: Option[String] = None,
                                query: Option[String] = None,
                                skip: Option[String] = None)

object PutAPISimpleFulltext {
  implicit val rw: ReaderWriter[PutAPISimpleFulltext] = ccRW
}