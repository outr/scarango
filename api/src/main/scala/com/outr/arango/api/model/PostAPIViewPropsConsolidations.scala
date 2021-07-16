package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIViewPropsConsolidations(`type`: Option[String] = None)

object PostAPIViewPropsConsolidations {
  implicit val rw: ReaderWriter[PostAPIViewPropsConsolidations] = ccRW
}