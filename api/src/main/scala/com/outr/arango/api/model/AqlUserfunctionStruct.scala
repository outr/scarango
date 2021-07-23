package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class AqlUserfunctionStruct(code: Option[String] = None,
                                 isDeterministic: Option[Boolean] = None,
                                 name: Option[String] = None)

object AqlUserfunctionStruct {
  implicit val rw: ReaderWriter[AqlUserfunctionStruct] = ccRW
}