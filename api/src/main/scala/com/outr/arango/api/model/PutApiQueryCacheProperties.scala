package com.outr.arango.api.model

import io.circe.Json


case class PutApiQueryCacheProperties(includeSystem: Option[Boolean] = None,
                                      maxEntrySize: Option[Long] = None,
                                      maxResults: Option[Long] = None,
                                      maxResultsSize: Option[Long] = None,
                                      mode: Option[String] = None)