package com.outr.arango.api.model

import io.circe.Json


case class KeyGeneratorType(allowUserKeys: Option[Boolean] = None,
                            lastValue: Option[Int] = None,
                            `type`: Option[String] = None)