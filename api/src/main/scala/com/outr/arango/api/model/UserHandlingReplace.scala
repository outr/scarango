package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class UserHandlingReplace(passwd: String,
                               active: Option[Boolean] = None,
                               extra: Option[Value] = None)

object UserHandlingReplace {
  implicit val rw: ReaderWriter[UserHandlingReplace] = ccRW
}