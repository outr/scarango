package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class GetAPIClusterEndpointsRc200(error: Boolean,
                                       code: Option[Long] = None,
                                       endpoints: Option[ClusterEndpointsStruct] = None)

object GetAPIClusterEndpointsRc200 {
  implicit val rw: ReaderWriter[GetAPIClusterEndpointsRc200] = ccRW
}