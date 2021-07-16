package com.outr.arango.api.model

import fabric._
import fabric.rw._

case class ClusterEndpointsStruct(endpoint: Option[String] = None)

object ClusterEndpointsStruct {
  implicit val rw: ReaderWriter[ClusterEndpointsStruct] = ccRW
}