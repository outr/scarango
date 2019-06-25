package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleByExample(collection: String,
                                 batchSize: Option[Long] = None,
                                 example: Option[String] = None,
                                 limit: Option[String] = None,
                                 skip: Option[String] = None)