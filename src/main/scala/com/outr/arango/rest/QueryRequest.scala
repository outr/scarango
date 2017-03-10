package com.outr.arango.rest

import io.circe.Json

case class QueryRequest(query: String,
                        bindVars: Json,
                        count: Boolean,
                        batchSize: Option[Int],
                        cache: Option[Boolean],
                        memoryLimit: Option[Long],
                        ttl: Option[Int],
                        options: QueryRequestOptions)

case class QueryRequestOptions(profile: Option[Boolean] = None,
                               `optimizer.rules`: Option[String] = None,
                               sateliteSyncWait: Double = 60.0,
                               fullCount: Option[Boolean] = None,
                               maxPlans: Option[Int] = None)