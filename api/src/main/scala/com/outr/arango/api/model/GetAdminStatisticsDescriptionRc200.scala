package com.outr.arango.api.model

import io.circe.Json


case class GetAdminStatisticsDescriptionRc200(groups: AdminStatisticsGroupStruct,
                                              code: Option[Long] = None,
                                              error: Option[Boolean] = None,
                                              figures: Option[AdminStatisticsFiguresStruct] = None)