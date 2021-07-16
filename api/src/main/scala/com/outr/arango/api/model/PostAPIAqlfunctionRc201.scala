package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIAqlfunctionRc201(error: Boolean,
                                   code: Option[Long] = None,
                                   isNewlyCreated: Option[Boolean] = None)

object PostAPIAqlfunctionRc201 {
  implicit val rw: ReaderWriter[PostAPIAqlfunctionRc201] = ccRW
}