package com.outr.arango.api.model

import io.circe.Json


case class GraphList(graph: Option[GraphRepresentation] = None)