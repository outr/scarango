package com.outr.arango.api.model

import io.circe.Json


case class SystemStatisticsStruct(majorPageFaults: Option[Int] = None,
                                  minorPageFaults: Option[Int] = None,
                                  numberOfThreads: Option[Int] = None,
                                  residentSize: Option[Int] = None,
                                  residentSizePercent: Option[Double] = None,
                                  systemTime: Option[Double] = None,
                                  userTime: Option[Double] = None,
                                  virtualSize: Option[Int] = None)