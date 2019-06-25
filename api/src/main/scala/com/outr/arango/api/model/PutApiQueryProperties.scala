package com.outr.arango.api.model

import io.circe.Json


case class PutApiQueryProperties(enabled: Boolean,
                                 maxQueryStringLength: Option[Long] = None,
                                 maxSlowQueries: Option[Long] = None,
                                 slowQueryThreshold: Option[Long] = None,
                                 trackBindVars: Option[Boolean] = None,
                                 trackSlowQueries: Option[Boolean] = None)