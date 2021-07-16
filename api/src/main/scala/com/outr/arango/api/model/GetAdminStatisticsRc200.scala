package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAdminStatisticsRc200(error: Boolean,
                                   client: Option[ClientStatisticsStruct] = None,
                                   code: Option[Long] = None,
                                   enabled: Option[Boolean] = None,
                                   errorMessage: Option[String] = None,
                                   http: Option[HttpStatisticsStruct] = None,
                                   server: Option[ServerStatisticsStruct] = None,
                                   system: Option[SystemStatisticsStruct] = None,
                                   time: Option[Long] = None)

object GetAdminStatisticsRc200 {
  implicit val rw: ReaderWriter[GetAdminStatisticsRc200] = ccRW
}