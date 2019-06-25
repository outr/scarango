package com.outr.arango.api.model

import io.circe.Json


case class ClusterEndpointsStruct(endpoint: Option[String] = None)