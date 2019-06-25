package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleFulltext(collection: String,
                                attribute: Option[String] = None,
                                index: Option[String] = None,
                                limit: Option[String] = None,
                                query: Option[String] = None,
                                skip: Option[String] = None)