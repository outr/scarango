package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class PostAPIViewPropsConsolidation()

object PostAPIViewPropsConsolidation {
  implicit val rw: ReaderWriter[PostAPIViewPropsConsolidation] = ccRW
}