package com.outr.arango.api.model

import io.circe.Json


case class CollectionFiguresAlive(count: Option[Long] = None,
                                  size: Option[Long] = None)