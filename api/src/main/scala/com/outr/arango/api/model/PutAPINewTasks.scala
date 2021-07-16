package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PutAPINewTasks(name: String,
                          command: Option[String] = None,
                          offset: Option[Long] = None,
                          params: Option[String] = None,
                          period: Option[Long] = None)

object PutAPINewTasks {
  implicit val rw: ReaderWriter[PutAPINewTasks] = ccRW
}