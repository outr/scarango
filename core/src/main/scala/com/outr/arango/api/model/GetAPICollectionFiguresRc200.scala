package com.outr.arango.api.model

import io.circe.Json

/**
  * GetAPICollectionFiguresRc200
  *
  * @param count The number of documents currently present in the collection.
  * @param figures *** No description ***
  * @param journalSize The maximal size of a journal or datafile in bytes.
  *
  * WARNING: This code is generated by youi-plugin's generateHttpClient. Do not modify directly.
  */
case class GetAPICollectionFiguresRc200(count: Long,
                                        figures: Option[CollectionFigures] = None,
                                        journalSize: Option[Long] = None)