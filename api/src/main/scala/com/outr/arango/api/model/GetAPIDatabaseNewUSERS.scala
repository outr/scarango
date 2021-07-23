package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAPIDatabaseNewUSERS(active: Option[Boolean] = None,
                                  extra: Option[Value] = None,
                                  passwd: Option[String] = None,
                                  username: Option[String] = None)

object GetAPIDatabaseNewUSERS {
  implicit val rw: ReaderWriter[GetAPIDatabaseNewUSERS] = ccRW
}