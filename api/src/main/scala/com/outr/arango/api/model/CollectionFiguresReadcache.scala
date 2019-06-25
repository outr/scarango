package com.outr.arango.api.model

import io.circe.Json


case class CollectionFiguresReadcache(count: Option[Long] = None,
                                      size: Option[Long] = None)