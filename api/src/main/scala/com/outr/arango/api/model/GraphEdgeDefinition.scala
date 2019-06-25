package com.outr.arango.api.model

import io.circe.Json


case class GraphEdgeDefinition(collection: Option[String] = None,
                               from: Option[List[String]] = None,
                               to: Option[List[String]] = None)