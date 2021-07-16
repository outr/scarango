package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPICursorRc201(error: Boolean,
                              cached: Option[Boolean] = None,
                              code: Option[Int] = None,
                              count: Option[Long] = None,
                              extra: Option[Value] = None,
                              hasMore: Option[Boolean] = None,
                              id: Option[String] = None,
                              result: Option[List[String]] = None)

object PostAPICursorRc201 {
  implicit val rw: ReaderWriter[PostAPICursorRc201] = ccRW
}