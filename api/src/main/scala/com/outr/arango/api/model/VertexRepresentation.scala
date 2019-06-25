package com.outr.arango.api.model

import io.circe.Json


case class VertexRepresentation(Id: Option[String] = None,
                                Key: Option[String] = None,
                                Rev: Option[String] = None)