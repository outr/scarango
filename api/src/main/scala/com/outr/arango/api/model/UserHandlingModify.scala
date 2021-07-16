package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class UserHandlingModify(passwd: String,
                              active: Option[Boolean] = None,
                              extra: Option[Value] = None)

object UserHandlingModify {
  implicit val rw: ReaderWriter[UserHandlingModify] = ccRW
}