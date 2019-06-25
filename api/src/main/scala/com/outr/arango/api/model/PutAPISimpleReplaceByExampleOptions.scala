package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleReplaceByExampleOptions(limit: Option[String] = None,
                                               waitForSync: Option[Boolean] = None)