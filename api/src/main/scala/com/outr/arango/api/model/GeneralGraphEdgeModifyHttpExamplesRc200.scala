package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeModifyHttpExamplesRc200(error: Boolean,
                                                   code: Option[Int] = None,
                                                   edge: Option[EdgeRepresentation] = None,
                                                   `new`: Option[EdgeRepresentation] = None,
                                                   old: Option[EdgeRepresentation] = None)