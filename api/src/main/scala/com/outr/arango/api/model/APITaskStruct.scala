package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class APITaskStruct(command: Option[String] = None,
                         created: Option[Double] = None,
                         database: Option[String] = None,
                         id: Option[String] = None,
                         name: Option[String] = None,
                         offset: Option[Double] = None,
                         period: Option[Double] = None,
                         `type`: Option[String] = None)

object APITaskStruct {
  implicit val rw: ReaderWriter[APITaskStruct] = ccRW
}