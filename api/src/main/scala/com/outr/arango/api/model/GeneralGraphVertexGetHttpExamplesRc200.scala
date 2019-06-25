package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphVertexGetHttpExamplesRc200(error: Boolean,
                                                  code: Option[Int] = None,
                                                  vertex: Option[VertexRepresentation] = None)