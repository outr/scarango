package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPINewTasksRc200(id: String,
                                code: Option[Double] = None,
                                command: Option[String] = None,
                                created: Option[Double] = None,
                                database: Option[String] = None,
                                error: Option[Boolean] = None,
                                offset: Option[Double] = None,
                                period: Option[Double] = None,
                                `type`: Option[String] = None)

object PostAPINewTasksRc200 {
  implicit val rw: ReaderWriter[PostAPINewTasksRc200] = ccRW
}