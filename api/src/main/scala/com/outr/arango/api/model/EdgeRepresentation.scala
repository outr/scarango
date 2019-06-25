package com.outr.arango.api.model

import io.circe.Json


case class EdgeRepresentation(From: Option[String] = None,
                              Id: Option[String] = None,
                              Key: Option[String] = None,
                              Rev: Option[String] = None,
                              To: Option[String] = None)