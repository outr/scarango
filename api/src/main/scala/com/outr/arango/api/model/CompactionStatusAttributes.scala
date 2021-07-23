package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class CompactionStatusAttributes(message: Option[String] = None,
                                      time: Option[String] = None)

object CompactionStatusAttributes {
  implicit val rw: ReaderWriter[CompactionStatusAttributes] = ccRW
}