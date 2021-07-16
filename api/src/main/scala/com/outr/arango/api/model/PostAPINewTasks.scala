package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPINewTasks(name: String,
                           command: Option[String] = None,
                           offset: Option[Long] = None,
                           params: Option[String] = None,
                           period: Option[Long] = None)

object PostAPINewTasks {
  implicit val rw: ReaderWriter[PostAPINewTasks] = ccRW
}