package com.outr.arango.api.model

import io.circe.Json


case class RestLookupByKeys(collection: String,
                            keys: Option[List[String]] = None)