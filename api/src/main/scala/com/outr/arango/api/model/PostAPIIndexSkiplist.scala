package com.outr.arango.api.model

import io.circe.Json


case class PostAPIIndexSkiplist(`type`: String,
                                deduplicate: Option[Boolean] = None,
                                fields: Option[List[String]] = None,
                                sparse: Option[Boolean] = None,
                                unique: Option[Boolean] = None)