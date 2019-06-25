package com.outr.arango.api.model

import io.circe.Json


case class PostAPIViewIresearch(name: String,
                                properties: Option[PostAPIViewProps] = None,
                                `type`: Option[String] = None)