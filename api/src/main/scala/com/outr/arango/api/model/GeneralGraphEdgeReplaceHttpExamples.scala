package com.outr.arango.api.model

import io.circe.Json


case class GeneralGraphEdgeReplaceHttpExamples(From: String,
                                               To: Option[String] = None)