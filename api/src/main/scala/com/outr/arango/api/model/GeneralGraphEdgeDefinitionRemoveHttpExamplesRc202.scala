package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeDefinitionRemoveHttpExamplesRc202(error: Boolean,
                                                             code: Option[Int] = None,
                                                             graph: Option[GraphRepresentation] = None)