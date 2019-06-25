package com.outr.arango.api.model

import io.circe.Json


case class PostAPIAnalyzer(name: String,
                           features: Option[List[String]] = None,
                           properties: Option[String] = None,
                           `type`: Option[String] = None)