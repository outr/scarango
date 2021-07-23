package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphCreateHttpExamplesRc409(error: Boolean,
                                               code: Option[Int] = None,
                                               errorMessage: Option[String] = None,
                                               errorNum: Option[Int] = None)

object GeneralGraphCreateHttpExamplesRc409 {
  implicit val rw: ReaderWriter[GeneralGraphCreateHttpExamplesRc409] = ccRW
}