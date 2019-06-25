package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeCreateHttpExamples(From: String,
                                              To: Option[String] = None)