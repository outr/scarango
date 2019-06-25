package com.outr.arango.api.model

import io.circe.Json


case class PostAPICursor(query: String,
                         batchSize: Option[Long] = None,
                         bindVars: Json,
                         cache: Option[Boolean] = None,
                         count: Option[Boolean] = None,
                         memoryLimit: Option[Long] = None,
                         options: Option[PostAPICursorOpts] = None,
                         ttl: Option[Long] = None)