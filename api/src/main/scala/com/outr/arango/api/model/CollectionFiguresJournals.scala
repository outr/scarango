package com.outr.arango.api.model

import io.circe.Json


case class CollectionFiguresJournals(count: Option[Long] = None,
                                     fileSize: Option[Long] = None)