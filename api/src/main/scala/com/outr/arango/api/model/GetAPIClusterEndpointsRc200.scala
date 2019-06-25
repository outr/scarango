package com.outr.arango.api.model

import io.circe.Json


case class GetAPIClusterEndpointsRc200(error: Boolean,
                                       code: Option[Long] = None,
                                       endpoints: Option[ClusterEndpointsStruct] = None)