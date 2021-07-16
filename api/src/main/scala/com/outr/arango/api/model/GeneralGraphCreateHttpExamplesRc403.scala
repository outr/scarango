package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphCreateHttpExamplesRc403(error: Boolean,
                                               code: Option[Int] = None,
                                               errorMessage: Option[String] = None,
                                               errorNum: Option[Int] = None)

object GeneralGraphCreateHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphCreateHttpExamplesRc403] = ccRW
}