package com.outr.arango.api.model

import io.circe.Json


case class PostAPICollectionOpts(allowUserKeys: Option[Boolean] = None,
                                 increment: Option[Long] = None,
                                 offset: Option[Long] = None,
                                 `type`: Option[String] = None)