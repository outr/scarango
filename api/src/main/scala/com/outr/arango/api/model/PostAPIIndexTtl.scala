package com.outr.arango.api.model

import io.circe.Json


case class PostAPIIndexTtl(`type`: String,
                           expireAfter: Option[Double] = None,
                           fields: Option[List[String]] = None)