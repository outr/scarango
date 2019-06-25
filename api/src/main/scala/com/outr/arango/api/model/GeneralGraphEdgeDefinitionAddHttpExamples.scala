package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeDefinitionAddHttpExamples(collection: String,
                                                     from: Option[List[String]] = None,
                                                     to: Option[List[String]] = None)