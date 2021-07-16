package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class AdminStatisticsGroupStruct(description: Option[String] = None,
                                      group: Option[String] = None,
                                      name: Option[String] = None)

object AdminStatisticsGroupStruct {
  implicit val rw: ReaderWriter[AdminStatisticsGroupStruct] = ccRW
}