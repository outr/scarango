package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class PostAPIAqlfunctionRc200(error: Boolean,
                                   code: Option[Long] = None,
                                   isNewlyCreated: Option[Boolean] = None)

object PostAPIAqlfunctionRc200 {
  implicit val rw: ReaderWriter[PostAPIAqlfunctionRc200] = ccRW
}