package com.outr.arango.api.model

import io.circe.Json


case class PostAPIExport(flush: Boolean,
                         batchSize: Option[Long] = None,
                         count: Option[Boolean] = None,
                         flushWait: Option[Long] = None,
                         limit: Option[Long] = None,
                         restrict: Option[PostAPIExportRestrictions] = None,
                         ttl: Option[Long] = None)