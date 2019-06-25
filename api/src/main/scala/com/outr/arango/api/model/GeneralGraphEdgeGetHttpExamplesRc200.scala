package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeGetHttpExamplesRc200(error: Boolean,
                                                code: Option[Int] = None,
                                                edge: Option[EdgeRepresentation] = None)