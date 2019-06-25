package com.outr.arango.api.model

import io.circe.Json


case class SetofStatisticsStruct(count: Option[Int] = None,
                                 counts: Option[List[Int]] = None,
                                 sum: Option[Double] = None)