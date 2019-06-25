package com.outr.arango.api.model

import io.circe.Json


case class GetAdminLogRc200(lid: List[String],
                            level: Option[String] = None,
                            text: Option[String] = None,
                            timestamp: Option[List[String]] = None,
                            topic: Option[String] = None,
                            totalAmount: Option[Long] = None)