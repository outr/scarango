package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class ExplainOptions(allPlans: Option[Boolean] = None,
                          maxNumberOfPlans: Option[Long] = None,
                          optimizerRules: Option[List[String]] = None)

object ExplainOptions {
  implicit val rw: ReaderWriter[ExplainOptions] = ccRW
}