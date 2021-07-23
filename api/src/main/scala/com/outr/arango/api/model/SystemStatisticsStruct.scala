package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class SystemStatisticsStruct(majorPageFaults: Option[Int] = None,
                                  minorPageFaults: Option[Int] = None,
                                  numberOfThreads: Option[Int] = None,
                                  residentSize: Option[Int] = None,
                                  residentSizePercent: Option[Double] = None,
                                  systemTime: Option[Double] = None,
                                  userTime: Option[Double] = None,
                                  virtualSize: Option[Int] = None)

object SystemStatisticsStruct {
  implicit val rw: ReaderWriter[SystemStatisticsStruct] = ccRW
}