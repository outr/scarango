package com.outr.arango.api.model

import io.circe.Json


case class PostAPITransaction(collections: String,
                              action: Option[String] = None,
                              lockTimeout: Option[Long] = None,
                              maxTransactionSize: Option[Long] = None,
                              params: Option[String] = None,
                              waitForSync: Option[Boolean] = None)