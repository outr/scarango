package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class UserHandlingCreate(user: String,
                              active: Option[Boolean] = None,
                              extra: Option[Value] = None,
                              passwd: Option[String] = None)

object UserHandlingCreate {
  implicit val rw: ReaderWriter[UserHandlingCreate] = ccRW
}