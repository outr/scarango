package com.outr.arango.api.model

import io.circe.Json


case class ExplainOptions(allPlans: Option[Boolean] = None,
                          maxNumberOfPlans: Option[Long] = None,
                          optimizerRules: Option[List[String]] = None)