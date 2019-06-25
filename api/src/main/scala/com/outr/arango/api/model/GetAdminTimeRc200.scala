package com.outr.arango.api.model

import io.circe.Json


case class GetAdminTimeRc200(error: Boolean,
                             code: Option[Long] = None,
                             time: Option[Double] = None)