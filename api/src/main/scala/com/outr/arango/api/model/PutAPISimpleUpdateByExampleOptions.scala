package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleUpdateByExampleOptions(keepNull: Option[Boolean] = None,
                                              limit: Option[Long] = None,
                                              mergeObjects: Option[Boolean] = None,
                                              waitForSync: Option[Boolean] = None)