package com.outr.arango.api.model

import io.circe.Json


case class PostAPIView(name: String,
                       properties: Option[PostAPIViewProps] = None,
                       `type`: Option[String] = None)