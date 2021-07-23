package com.outr.arango.api.model

import fabric.rw.{ReaderWriter, ccRW}

case class PostAPIAqlfunction(name: String,
                              code: Option[String] = None,
                              isDeterministic: Option[Boolean] = None)

object PostAPIAqlfunction {
  implicit val rw: ReaderWriter[PostAPIAqlfunction] = ccRW
}