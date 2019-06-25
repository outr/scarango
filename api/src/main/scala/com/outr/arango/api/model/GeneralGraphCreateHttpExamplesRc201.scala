package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphCreateHttpExamplesRc201(error: Boolean,
                                               code: Option[Int] = None,
                                               graph: Option[GraphRepresentation] = None)