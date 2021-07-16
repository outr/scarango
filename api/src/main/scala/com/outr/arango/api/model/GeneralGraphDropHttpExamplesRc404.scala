package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphDropHttpExamplesRc404(error: Boolean,
                                             code: Option[Int] = None,
                                             errorMessage: Option[String] = None,
                                             errorNum: Option[Int] = None)

object GeneralGraphDropHttpExamplesRc404 {
  implicit val rw: ReaderWriter[GeneralGraphDropHttpExamplesRc404] = ccRW
}