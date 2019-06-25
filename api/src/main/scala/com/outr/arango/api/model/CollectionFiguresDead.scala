package com.outr.arango.api.model

import io.circe.Json


case class CollectionFiguresDead(count: Option[Long] = None,
                                 deletion: Option[Long] = None,
                                 size: Option[Long] = None)