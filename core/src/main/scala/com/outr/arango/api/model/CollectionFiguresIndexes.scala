package com.outr.arango.api.model

import io.circe.Json

/**
  * CollectionFiguresIndexes
  *
  * @param count The total number of indexes defined for the collection, including the pre-defined
  *        indexes (e.g. primary index).
  * @param size The total memory allocated for indexes in bytes.
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class CollectionFiguresIndexes(count: Option[Long] = None,
                                    size: Option[Long] = None)