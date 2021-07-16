package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class SetofStatisticsStruct(count: Option[Int] = None,
                                 counts: Option[List[Int]] = None,
                                 sum: Option[Double] = None)

object SetofStatisticsStruct {
  implicit val rw: ReaderWriter[SetofStatisticsStruct] = ccRW
}