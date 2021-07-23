package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GeneralGraphListHttpExamplesRc200(error: Boolean,
                                             code: Option[Int] = None,
                                             graphs: Option[GraphList] = None)

object GeneralGraphListHttpExamplesRc200 {
  implicit val rw: ReaderWriter[GeneralGraphListHttpExamplesRc200] = ccRW
}