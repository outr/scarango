package com.outr.arango.api.model

import io.circe.Json


case class GetAdminStatisticsRc200(error: Boolean,
                                   client: Option[ClientStatisticsStruct] = None,
                                   code: Option[Long] = None,
                                   enabled: Option[Boolean] = None,
                                   errorMessage: Option[String] = None,
                                   http: Option[HttpStatisticsStruct] = None,
                                   server: Option[ServerStatisticsStruct] = None,
                                   system: Option[SystemStatisticsStruct] = None,
                                   time: Option[Long] = None)