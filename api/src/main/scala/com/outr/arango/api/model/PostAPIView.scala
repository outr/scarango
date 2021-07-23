package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIView(name: String,
                       properties: Option[PostAPIViewProps] = None,
                       `type`: Option[String] = None)

object PostAPIView {
  implicit val rw: ReaderWriter[PostAPIView] = ccRW
}