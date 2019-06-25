package com.outr.arango.api.model

import io.circe.Json


case class AqlUserfunctionStruct(code: Option[String] = None,
                                 isDeterministic: Option[Boolean] = None,
                                 name: Option[String] = None)