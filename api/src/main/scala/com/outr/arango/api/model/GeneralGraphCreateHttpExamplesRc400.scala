package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphCreateHttpExamplesRc400(error: Boolean,
                                               code: Option[Int] = None,
                                               errorMessage: Option[String] = None,
                                               errorNum: Option[Int] = None)

object GeneralGraphCreateHttpExamplesRc400 {
  implicit val rw: ReaderWriter[GeneralGraphCreateHttpExamplesRc400] = ccRW
}