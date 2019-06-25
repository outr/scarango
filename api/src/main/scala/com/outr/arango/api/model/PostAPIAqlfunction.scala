package com.outr.arango.api.model

import io.circe.Json


case class PostAPIAqlfunction(name: String,
                              code: Option[String] = None,
                              isDeterministic: Option[Boolean] = None)