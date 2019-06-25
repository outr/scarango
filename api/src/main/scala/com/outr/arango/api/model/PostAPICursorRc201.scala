package com.outr.arango.api.model

import io.circe.Json


case class PostAPICursorRc201(error: Boolean,
                              cached: Option[Boolean] = None,
                              code: Option[Int] = None,
                              count: Option[Long] = None,
                              extra: Option[Json] = None,
                              hasMore: Option[Boolean] = None,
                              id: Option[String] = None,
                              result: Option[List[String]] = None)