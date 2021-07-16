package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAdminStatisticsDescriptionRc200(groups: AdminStatisticsGroupStruct,
                                              code: Option[Long] = None,
                                              error: Option[Boolean] = None,
                                              figures: Option[AdminStatisticsFiguresStruct] = None)

object GetAdminStatisticsDescriptionRc200 {
  implicit val rw: ReaderWriter[GetAdminStatisticsDescriptionRc200] = ccRW
}