package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class UserHandlingGrantDatabase(grant: String)

object UserHandlingGrantDatabase {
  implicit val rw: ReaderWriter[UserHandlingGrantDatabase] = ccRW
}