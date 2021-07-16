package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class V8ContextStruct(available: Option[Int] = None,
                           busy: Option[Int] = None,
                           dirty: Option[Int] = None,
                           free: Option[Int] = None,
                           max: Option[Int] = None)

object V8ContextStruct {
  implicit val rw: ReaderWriter[V8ContextStruct] = ccRW
}