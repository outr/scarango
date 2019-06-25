package com.outr.arango.api.model

import io.circe.Json


case class PutAPISimpleRange(collection: String,
                             attribute: Option[String] = None,
                             closed: Option[Boolean] = None,
                             left: Option[String] = None,
                             limit: Option[Long] = None,
                             right: Option[String] = None,
                             skip: Option[String] = None)