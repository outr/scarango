package com.outr.arango.api.model

import io.circe.Json


case class PostAPIGharialCreateOpts(numberOfShards: Option[Int] = None,
                                    replicationFactor: Option[Int] = None,
                                    smartGraphAttribute: Option[String] = None)