package com.outr.arango.api.model

import io.circe.Json


case class GetAPICollectionFiguresRc200(count: Long,
                                        figures: Option[CollectionFigures] = None,
                                        journalSize: Option[Long] = None)