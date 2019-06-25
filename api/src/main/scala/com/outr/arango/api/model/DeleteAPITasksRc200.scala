package com.outr.arango.api.model

import io.circe.Json


case class DeleteAPITasksRc200(code: Double,
                               error: Option[Boolean] = None)