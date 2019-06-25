package com.outr.arango.api.model

import io.circe.Json


case class PostAPIExplain(query: String,
                          bindVars: Option[List[String]] = None,
                          options: Option[ExplainOptions] = None)