package com.outr.arango.api.model

import io.circe.Json


case class CollectionFiguresRevisions(count: Option[Long] = None,
                                      size: Option[Long] = None)