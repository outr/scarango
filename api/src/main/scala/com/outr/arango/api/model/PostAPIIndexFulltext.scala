package com.outr.arango.api.model

import io.circe.Json


case class PostAPIIndexFulltext(`type`: String,
                                fields: Option[List[String]] = None,
                                minLength: Option[Long] = None)