package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphDropHttpExamplesRc403(error: Boolean,
                                             code: Option[Int] = None,
                                             errorMessage: Option[String] = None,
                                             errorNum: Option[Int] = None)

object GeneralGraphDropHttpExamplesRc403 {
  implicit val rw: ReaderWriter[GeneralGraphDropHttpExamplesRc403] = ccRW
}