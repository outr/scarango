package com.outr.arango.api.model

import io.circe.Json


case class DeleteAPITasksRc404(code: Double,
                               error: Option[Boolean] = None,
                               errorMessage: Option[String] = None)