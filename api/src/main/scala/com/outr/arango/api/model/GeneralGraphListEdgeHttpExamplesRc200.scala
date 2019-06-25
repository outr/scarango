package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphListEdgeHttpExamplesRc200(error: Boolean,
                                                 code: Option[Int] = None,
                                                 collections: Option[List[String]] = None)