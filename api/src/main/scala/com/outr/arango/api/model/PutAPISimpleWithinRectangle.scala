package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleWithinRectangle(collection: String,
                                       geo: Option[String] = None,
                                       latitude1: Option[String] = None,
                                       latitude2: Option[String] = None,
                                       limit: Option[String] = None,
                                       longitude1: Option[String] = None,
                                       longitude2: Option[String] = None,
                                       skip: Option[String] = None)