package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphVertexDeleteHttpExamplesRc200(error: Boolean,
                                                     code: Option[Int] = None,
                                                     old: Option[VertexRepresentation] = None,
                                                     removed: Option[Boolean] = None)