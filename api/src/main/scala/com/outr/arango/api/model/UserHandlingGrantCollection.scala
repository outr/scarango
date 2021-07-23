package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class UserHandlingGrantCollection(grant: String)

object UserHandlingGrantCollection {
  implicit val rw: ReaderWriter[UserHandlingGrantCollection] = ccRW
}