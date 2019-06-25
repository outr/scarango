package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphCreateHttpExamples(name: String,
                                          edgeDefinitions: Option[GraphEdgeDefinition] = None,
                                          isSmart: Option[Boolean] = None,
                                          options: Option[PostAPIGharialCreateOpts] = None)