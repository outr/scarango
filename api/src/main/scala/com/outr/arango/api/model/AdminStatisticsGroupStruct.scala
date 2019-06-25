package com.outr.arango.api.model

import io.circe.Json


case class AdminStatisticsGroupStruct(description: Option[String] = None,
                                      group: Option[String] = None,
                                      name: Option[String] = None)