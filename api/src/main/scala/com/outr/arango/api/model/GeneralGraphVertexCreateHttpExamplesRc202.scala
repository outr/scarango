package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphVertexCreateHttpExamplesRc202(error: Boolean,
                                                     code: Option[Int] = None,
                                                     `new`: Option[VertexRepresentation] = None,
                                                     vertex: Option[VertexRepresentation] = None)