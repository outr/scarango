package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphVertexCreateHttpExamplesRc201(error: Boolean,
                                                     code: Option[Int] = None,
                                                     `new`: Option[VertexRepresentation] = None,
                                                     vertex: Option[VertexRepresentation] = None)