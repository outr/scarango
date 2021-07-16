package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIViewIresearch(name: String,
                                properties: Option[PostAPIViewProps] = None,
                                `type`: Option[String] = None)

object PostAPIViewIresearch {
  implicit val rw: ReaderWriter[PostAPIViewIresearch] = ccRW
}