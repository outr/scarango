package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeDeleteHttpExamplesRc202(error: Boolean,
                                                   code: Option[Int] = None,
                                                   old: Option[EdgeRepresentation] = None,
                                                   removed: Option[Boolean] = None)